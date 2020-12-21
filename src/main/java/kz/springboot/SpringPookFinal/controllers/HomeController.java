package kz.springboot.SpringPookFinal.controllers;

import kz.springboot.SpringPookFinal.entities.*;
import kz.springboot.SpringPookFinal.services.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CartService cartService;

    @Autowired
    private SoldItemsService soldItemsService;

    @Autowired
    private HttpSession session;

    @Value("${file.item.viewPath}")
    private String viewItemPath;

    @Value("${file.item.uploadPath}")
    private String uploadItemPath;

    @Value("${file.avatar.viewPath}")
    private String viewPath;

    @Value("${file.avatar.uploadPath}")
    private String uploadPath;

    @Value("${file.avatar.defaultPicture}")
    private String defaultPicture;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/")
    public String index(Model model,
                        @CookieValue(name = "order", defaultValue = "asc") String order) {
        model.addAttribute("currentUser", getUserData());
        List<Items> allItems = itemService.getItems();
        if (order.equals("asc")) {
            allItems = itemService.getItemsOrderAsc("");
        } else {
            allItems = itemService.getItemsOrderDesc("");
        }
        List<Brands> brands = itemService.getAllBrands();
        List<Countries> countries = itemService.getAllCountries();
        List<Categories> categories = itemService.getAllCategories();
        model.addAttribute("countries", countries);
        model.addAttribute("brands", brands);
        model.addAttribute("allItems", allItems);
        model.addAttribute("categories", categories);
        return "index";
    }

    @PostMapping(value = "/filter")
    public String filter(Model model,
                         @CookieValue(name = "order", defaultValue = "asc") String order,
                         @RequestParam(name = "filterName", defaultValue = "") String name,
                         @RequestParam(name = "brand_id", defaultValue = "0") Long br_id,
                         @RequestParam(name = "priceFrom", defaultValue = "0") double priceFrom,
                         @RequestParam(name = "priceTo", defaultValue = "10000000") double priceTo) {
        List<Items> items = itemService.getItems();


        if (order.equals("asc")) {
            if (br_id == 0) {
                items = itemService.getItemsByNameContainingAndPriceBetweenAsc(name, priceFrom, priceTo);
            } else {
                items = itemService.getItemsByNameAndBrandPriceBetweenAsc(name, itemService.getBrand(br_id), priceFrom, priceTo);
            }
        } else {
            if (br_id == 0) {
                items = itemService.getItemsByNameContainingAndPriceBetweenDesc(name, priceFrom, priceTo);
            } else {
                items = itemService.getItemsByNameAndBrandPriceBetweenDesc(name, itemService.getBrand(br_id), priceFrom, priceTo);
            }
        }
        model.addAttribute("brands", itemService.getAllBrands());
        model.addAttribute("filtered", items);
        model.addAttribute("categories", itemService.getAllCategories());

        return "filtered";
    }

    @PostMapping(value = "order")
    public String order(HttpServletResponse response,
                        @RequestParam(name = "order", defaultValue = "asc") String order) {
        Cookie cookie = new Cookie("order", order);
        try {
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String add(@RequestParam(name = "name") String name,
                      @RequestParam(name = "description") String description,
                      @RequestParam(name = "price") Double price,
                      @RequestParam(name = "stars") Integer stars,
                      @RequestParam(name = "smallPic") String smallPic,
                      @RequestParam(name = "bigPic") String bigPic,
                      @RequestParam(name = "brand_id") Long brand_id,RedirectAttributes redirectAttributes) {
        Items item = new Items();
        item.setName(name);
        item.setDescription(description);
        item.setStars(stars);
        item.setPrice(price);
        item.setLargePicURL(bigPic);
        item.setSmallPicURL(smallPic);
        item.setQuantity(10);
        item.setBrand(itemService.getBrand(brand_id));
        redirectAttributes.addFlashAttribute("success", "Item added!");
        itemService.addItem(item);
        return "redirect:/admin/items";
    }

    @GetMapping(value = "/details/{object}/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String details(Model model, @PathVariable(name = "id") Long id,
                          @PathVariable(name = "object") String object) {
        model.addAttribute("currentUser", getUserData());
        switch (object) {
            case "roles":
                Roles role = userService.getRoleById(id);
                model.addAttribute("role", role);
                break;
            case "users":
                Users user = userService.getUserById(id);
                model.addAttribute("user", user);
                List<Roles> roles = userService.getRoles();
                roles.removeAll(user.getRoles());
                model.addAttribute("roles", roles);
                break;
            case "items":
                Items item = itemService.getItem(id);
                model.addAttribute("item", item);
                model.addAttribute("itemphotos", itemService.getAllPicturesByItem(item));
//                model.addAttribute("item_categories", itemService.getItem(id).getCategories());

                List<Categories> categories = itemService.getAllCategories();
                categories.removeAll(item.getCategories());
                model.addAttribute("categories", categories);
                break;
            case "countries":
                model.addAttribute("country", itemService.getCountry(id));
                break;
            case "brands":
                model.addAttribute("brand", itemService.getBrand(id));
                break;
            case "categories":
                model.addAttribute("category", itemService.getCategory(id));
                break;
            case "carts":
                return "redirect:/cart/"+id;
        }


        model.addAttribute("countries", itemService.getAllCountries());
        model.addAttribute("brands", itemService.getAllBrands());

        return "details";
    }




    @PostMapping(value = "/save")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String save(
            @RequestParam(name = "id", defaultValue = "0") Long id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "price") Double price,
            @RequestParam(name = "smallPic") String smallPic,
            @RequestParam(name = "bigPic") String bigPic,
            @RequestParam(name = "inTop") Boolean inTop,
            @RequestParam(name = "brand_id") Long br_id,RedirectAttributes redirectAttributes) {

        Items item = itemService.getItem(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setSmallPicURL(smallPic);
        item.setLargePicURL(bigPic);
        item.setInTopPage(inTop);
        item.setBrand(itemService.getBrand(br_id));
        itemService.saveItem(item);

        redirectAttributes.addFlashAttribute("success", "Item saved");
        return "redirect:/admin";
    }

    @PostMapping(value = "/saveBrand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String save(
            @RequestParam(name = "id", defaultValue = "0") Long id,
            @RequestParam(name = "nameBrand", defaultValue = "No brand") String name,
            @RequestParam(name = "country_id", defaultValue = "0") Long c_id,RedirectAttributes redirectAttributes) {

        Brands brand = itemService.getBrand(id);
        brand.setName(name);

        brand.setCountry(itemService.getCountry(c_id));
        redirectAttributes.addFlashAttribute("success", "Brand saved");
        itemService.saveBrand(brand);
        return "redirect:/admin";
    }

    @PostMapping(value = "/saveCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String save(
            @RequestParam(name = "id", defaultValue = "0") Long id,
            @RequestParam(name = "nameCountry", defaultValue = "No country") String name,
            @RequestParam(name = "code", defaultValue = "No code") String code,RedirectAttributes redirectAttributes) {

        Countries country = itemService.getCountry(id);
        country.setName(name);
        country.setCode(code);

        itemService.saveCountry(country);
        redirectAttributes.addFlashAttribute("success", "Country saved");
        return "redirect:/admin";
    }

    @PostMapping(value = "/saveCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String save(
            @RequestParam(name = "id", defaultValue = "0") Long id,
            @RequestParam(name = "nameCategory", defaultValue = "No category") String name,RedirectAttributes redirectAttributes) {

        Categories categories = itemService.getCategory(id);
        categories.setName(name);

        itemService.saveCategory(categories);
        redirectAttributes.addFlashAttribute("success", "Category saved");
        return "redirect:/admin/categories";
    }

    @GetMapping(value = "/delete/{object}/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String delete(Model model,
                         @PathVariable(name = "id") Long id,
                         @PathVariable(name = "object") String object,RedirectAttributes redirectAttributes) {

        model.addAttribute("currentUser", getUserData());
        switch (object) {
            case "user":
                Users user = userService.getUserById(id);
                List<Roles> userRoles = user.getRoles();
                boolean admin = false;
                for (Roles r: userRoles)
                {

                    if(r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_MODERATOR")){
                        redirectAttributes.addFlashAttribute("error", "User is admin or moderator, DONT TOUCH IT");
                        admin = true;
                        break;
                    }
                }
                if(!admin){
                    userService.deleteUser(id);
                    redirectAttributes.addFlashAttribute("success", "User deleted");
                    break;
                }
                break;
            case "role":
                userService.deleteRole(id);
                redirectAttributes.addFlashAttribute("success", "Role deleted");
                break;
            case "item":
                itemService.deleteItem(itemService.getItem(id));
                redirectAttributes.addFlashAttribute("success", "Item deleted");
                break;
            case "country":
                itemService.deleteCountry(id);
                redirectAttributes.addFlashAttribute("success", "Country deleted");
                break;
            case "brand":
                itemService.deleteBrand(id);
                redirectAttributes.addFlashAttribute("success", "Brand deleted");
                break;
            case "category":
                itemService.deleteCategory(id);
                redirectAttributes.addFlashAttribute("success", "Category deleted");
                break;
        }
        return "redirect:/admin";
    }

    @GetMapping(value = "/brands/{id}")
    public String brands(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("brands", itemService.getAllBrands());
        model.addAttribute("items", itemService.getItemsByBrandId(id));
        model.addAttribute("categories", itemService.getAllCategories());
        return "branded";
    }

    @GetMapping(value = "/categories/{id}")
    public String categories(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("currentUser", getUserData());
        List<Items> items = itemService.getItems();
        Categories category = itemService.getCategory(id);

        List<Items> cat_items = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            boolean exist = false;
            List<Categories> item_categories = items.get(i).getCategories();
            if (item_categories != null) {
                for (Categories cat : item_categories) {
                    if (cat.getName().equals(category.getName())) {
                        exist = true;
                        break;
                    }
                }

            }
            if (exist) {
                cat_items.add(items.get(i));
            }
        }


        model.addAttribute("brands", itemService.getAllBrands());
        model.addAttribute("items", cat_items);
        model.addAttribute("categories", itemService.getAllCategories());
        return "branded";
    }

    @PostMapping(value = "/add_country")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addCountry(@RequestParam(name = "name") String name,
                             @RequestParam(name = "code") String code,RedirectAttributes redirectAttributes) {
        Countries cnt = new Countries();
        cnt.setCode(code);
        cnt.setName(name);

        itemService.saveCountry(cnt);
        redirectAttributes.addFlashAttribute("success", "Country added");
        return "redirect:/admin/countries";
    }

    @PostMapping(value = "/add_category")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addCategory(@RequestParam(name = "name") String name,RedirectAttributes redirectAttributes) {
        Categories category = new Categories();
        category.setName(name);

        itemService.addCategory(category);
        redirectAttributes.addFlashAttribute("success", "Category added");
        return "redirect:/admin/categories";
    }

    @PostMapping(value = "/add_brand")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addBrand(@RequestParam(name = "name") String name,
                           @RequestParam(name = "country_id") Long id,RedirectAttributes redirectAttributes) {
        Brands br = new Brands();
        br.setName(name);

        br.setCountry(itemService.getCountry(id));

        itemService.saveBrand(br);
        redirectAttributes.addFlashAttribute("success", "Brand added");
        return "redirect:/admin/brands";
    }

    @GetMapping(value = "/admin/{object}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String admin(Model model, @PathVariable(name = "object") String object) {
        model.addAttribute("currentUser", getUserData());
        switch (object) {
            case "roles":
                model.addAttribute("roles", userService.getRoles());
                break;
            case "users":
                model.addAttribute("users", userService.getUsers());
                break;
            case "items":
                model.addAttribute("items", itemService.getItems());
                break;
            case "countries":
                model.addAttribute("countries", itemService.getAllCountries());
                break;
            case "brands":
                model.addAttribute("brands", itemService.getAllBrands());
                break;
            case "categories":
                model.addAttribute("categories", itemService.getAllCategories());
                break;
            case "carts":
                model.addAttribute("carts", cartService.getCarts());
                break;
        }

        model.addAttribute("add_brands", itemService.getAllBrands());
        model.addAttribute("add_countries", itemService.getAllCountries());
        model.addAttribute("add_categories", itemService.getAllCountries());
        return "admin";
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String admin(Model model) {
        model.addAttribute("items", itemService.getItems());
        return "admin";
    }

    @PostMapping(value = "assigncategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MODERATOR')")
    public String assignCategory(@RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "cat_id") Long cat_id,
                                 RedirectAttributes redirectAttributes) {
        Categories cat = itemService.getCategory(cat_id);
        if (cat != null) {
            Items item = itemService.getItem(item_id);
            if (item != null) {
                List<Categories> categories = item.getCategories();
                boolean exist = false;
                if (categories == null) {
                    categories = new ArrayList<>();
                }

                for (Categories category : categories) {
                    if (category.getName().equals(cat.getName())) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    categories.add(cat);
                    item.setCategories(categories);

                    itemService.saveItem(item);
                    redirectAttributes.addFlashAttribute("success", "Category assigned");
                    return "redirect:/details/items/" + item_id;
                }
            }
        }
        redirectAttributes.addFlashAttribute("error", "Category is exist");
        return "redirect:/details/items/" + item_id;
    }

    @PostMapping(value = "deleteCategory")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String deleteCategory(@RequestParam(name = "item_id") Long item_id,
                                 @RequestParam(name = "cat_id") Long cat_id,
                                 RedirectAttributes redirectAttributes) {
        Categories cat = itemService.getCategory(cat_id);
        if (cat != null) {
            Items item = itemService.getItem(item_id);
            if (item != null) {
                List<Categories> categories = item.getCategories();
                if (categories == null) {
                    categories = new ArrayList<>();
                }
                categories.remove(cat);
                item.setCategories(categories);

                itemService.saveItem(item);
                redirectAttributes.addFlashAttribute("success","Category has deleted");
                return "redirect:/details/items/" + item_id;
            }
        }
        return "redirect:/admin/categories";
    }

    @GetMapping(value = "/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "403";
    }

    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "login";
    }

    @GetMapping(value = "/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "profile";
    }

    private Users getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User secUser = (User) authentication.getPrincipal();
            Users myUser = userService.getUserByEmail(secUser.getUsername());
            return myUser;
        }
        return null;
    }

    @GetMapping(value = "/register")
    public String register(Model model) {
        model.addAttribute("currentUser", getUserData());
        return "register";
    }

    @PostMapping(value = "/register")
    public String register(@RequestParam(name = "user_email") String user_email,
                           @RequestParam(name = "user_password") String user_password,
                           @RequestParam(name = "confirm_password") String confirm_password,
                           @RequestParam(name = "user_fullname") String user_fullname,
                           RedirectAttributes redirectAttributes) {

        if (user_password.equals(confirm_password) && isUserExist(user_email)==false) {

            List<Roles> roles = new ArrayList<>();
            Roles role = new Roles();
            role.setRole("ROLE_USER");
            roles.add(role);

            Users user = new Users();
            user.setEmail(user_email);
            user.setFullName(user_fullname);
            user.setPassword(bCryptPasswordEncoder.encode(user_password));

            userService.addUser(user);
            redirectAttributes.addFlashAttribute("success", "User has registered!");
            return "redirect:/login?success";
        } else {
            redirectAttributes.addFlashAttribute("error", "User exists!");
            return "redirect:/register?notmatch";}
    }
    public boolean isUserExist(String email){

        List<Users> users = userService.getUsers();
        boolean exist = false;
        for(Users user: users){
            if(user.getEmail().equals(email)){
                exist = true;
                break;
            }
        }

        return exist;
    }
    @PostMapping(value = "/addUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addUser(@RequestParam(name = "user_email") String user_email,
                          @RequestParam(name = "user_password") String user_password,
                          @RequestParam(name = "user_fullname") String user_fullname,
                          RedirectAttributes redirectAttributes) {


        List<Roles> roles = new ArrayList<>();
        Roles role = new Roles();
        role.setRole("ROLE_USER");
        roles.add(role);

        Users user = new Users();
        user.setEmail(user_email);
        user.setFullName(user_fullname);
        user.setPassword(bCryptPasswordEncoder.encode(user_password));

        userService.addUser(user);
        redirectAttributes.addFlashAttribute("success", "User has been added!");
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/saveUser")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String saveUser(@RequestParam(name = "id") Long id,
                           @RequestParam(name = "user_email") String user_email,
                          @RequestParam(name = "user_password") String user_password,
                          @RequestParam(name = "user_fullname") String user_fullname,
                           RedirectAttributes redirectAttributes) {




        Users user = userService.getUserById(id);

        user.setEmail(user_email);
        user.setFullName(user_fullname);
        user.setPassword(bCryptPasswordEncoder.encode(user_password));

        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success","User has been saved");
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/addRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addRole(@RequestParam(name = "role")String role_name,
                          RedirectAttributes redirectAttributes){
        Roles role = new Roles();
        role.setRole(role_name);
        userService.addRole(role);
        redirectAttributes.addFlashAttribute("success", "Role has been added!");
        return "redirect:/admin/roles";
    }
    @PostMapping(value = "/saveRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String addRole(@RequestParam(name = "id")Long id,
                          @RequestParam(name = "role")String role_name,
                          RedirectAttributes redirectAttributes){
        Roles role = userService.getRoleById(id);
        role.setRole(role_name);
        userService.saveRole(role);
        redirectAttributes.addFlashAttribute("success","Role has been saved!");
        return "redirect:/admin/roles";
    }

    @PostMapping(value = "/assignRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String assignRole(@RequestParam(name = "user_id") Long user_id,
                                 @RequestParam(name = "role_id") Long role_id,
                             RedirectAttributes redirectAttributes) {
        Roles role = userService.getRoleById(role_id);
        if (role != null) {
            Users user = userService.getUserById(user_id);
            if (user != null) {
                List<Roles> roles = user.getRoles();
                boolean exist = false;
                if (roles == null) {
                    roles = new ArrayList<>();
                }

                for (Roles r : roles) {
                    if (r.getRole().equals(role.getRole())) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    roles.add(role);
                    user.setRoles(roles);

                    userService.saveUser(user);
                    redirectAttributes.addFlashAttribute("success","Role has been assigned!");
                    return "redirect:/details/users/" + user_id;
                }
            }
        }
        return "redirect:/details/users/" + user_id;
    }

    @PostMapping(value = "/deleteRole")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String deleteRole(@RequestParam(name = "user_id") Long user_id,
                                 @RequestParam(name = "role_id") Long role_id,
                             RedirectAttributes redirectAttributes) {
        Roles role = userService.getRoleById(role_id);
        if (role != null) {
            Users user = userService.getUserById(user_id);
            if (user != null) {
                List<Roles> roles = user.getRoles();
                if (roles == null) {
                    roles = new ArrayList<>();
                }
                roles.remove(role);
                user.setRoles(roles);

                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("success", "Role has been deleted from user!");
                return "redirect:/details/users/" + user_id;
            }
        }
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/updateProfile")
    public String updateProfile(Model model,
                                @RequestParam(name = "id")Long id,
                                @RequestParam(name = "user_email")String email,
                                @RequestParam(name = "user_fullname")String fullname,
                                RedirectAttributes redirectAttributes){
        Users user = userService.getUserById(id);
        user.setEmail(email);
        user.setFullName(fullname);
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("success", "Profile has been updated");
        return "redirect:/profile";
    }
    @PostMapping(value = "/updatePassword")
    public String updatePassword(Model model,
                                @RequestParam(name = "id")Long id,
                                @RequestParam(name = "old_password")String old,
                                @RequestParam(name = "user_password")String user_password,
                                @RequestParam(name = "confirm_password")String confirm_password,
                                 RedirectAttributes redirectAttributes){

        Users user = userService.getUserById(id);
        if(user_password.equals(confirm_password) && bCryptPasswordEncoder.matches(old, user.getPassword())){
            user.setPassword(bCryptPasswordEncoder.encode(user_password));
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("success", "Password has been updated!");

            return "redirect:/profile?success";
        }else {
            redirectAttributes.addFlashAttribute("error", "Password hasn't been updated!");
            return "redirect:/profile?passerror";
        }

    }
    @PostMapping(value = "/uploadAvatar")
    @PreAuthorize("isAuthenticated()")
    public String uploadAvatar(@RequestParam(name = "user_ava")MultipartFile file, RedirectAttributes redirectAttributes){
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")){
            try {

                Users currentUser = getUserData();
                String picName = DigestUtils.sha1Hex("avatar_"+currentUser.getId()+"_!Picture");

                DigestUtils.sha1("qweqwe");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath+picName+".jpg");
                Files.write(path, bytes);

                currentUser.setUserAvatar(picName);
                userService.saveUser(currentUser);
                redirectAttributes.addFlashAttribute("success", "Avatar has been updated");
                return "redirect:/profile?success";
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "viewphoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewProfilePhoto(@PathVariable(name = "url")String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;

        if(url!=null && !url.equals("null")){
            pictureURL = viewPath + url + ".jpg";
        }else{
            pictureURL = viewPath + "default" + ".jpg";
        }
        InputStream in;

        try{

            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewPath+defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "viewItemPhoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public @ResponseBody byte[] viewItemPhoto(@PathVariable(name = "url")String url) throws IOException {
        String pictureURL = viewItemPath;

        if(url!=null){
            pictureURL = viewItemPath + url + ".jpg";
        }
        InputStream in;

        try{

            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        }catch (Exception e){
            ClassPathResource resource = new ClassPathResource(viewItemPath);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/details/{id}")
    public String details(Model model, @PathVariable(name = "id") Long id) {
        model.addAttribute("currentUser", getUserData());
        model.addAttribute("item", itemService.getItem(id));
        model.addAttribute("brands", itemService.getAllBrands());
        model.addAttribute("categories", itemService.getAllCategories());
        List<Pictures> pictures = itemService.getAllPicturesByItem(itemService.getItem(id));
        model.addAttribute("itemphotos", pictures);

        Items item = itemService.getItem(id);
        model.addAttribute("comments", commentService.getAllCommentsByItem(item));
        return "detailItem";
    }

    @PostMapping(value = "/deletePhoto")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String deletePhoto(@RequestParam(name = "item_id") Long item_id,
                              @RequestParam(name = "pic_id") Long pic_id,
                              RedirectAttributes redirectAttributes){

        Items item = itemService.getItem(item_id);
        Pictures pic = itemService.getPicture(pic_id);
        itemService.deletePicture(pic);
        redirectAttributes.addFlashAttribute("success", "Photo has been deleted");
        return "redirect:/details/items/"+item_id;
    }

    @PostMapping(value = "/addPhoto")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public String addPhoto(@RequestParam(name = "item_photo")MultipartFile file, @RequestParam(name = "item_id") Long item_id,
                           RedirectAttributes redirectAttributes){
        if(file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png")){
            try {

                Items item = itemService.getItem(item_id);
                if(item!=null){
                    try {
                        Pictures picture = new Pictures(0L, null, item);
                        String picName = DigestUtils.sha1Hex("itemPhoto"+ LocalDateTime.now()+ item.getId()+"_!Picture");
                        picture.setUrl(picName);

                        byte[] bytes = file.getBytes();
                        Path path = Paths.get(uploadItemPath+picName+".jpg");
                        Files.write(path, bytes);
                        itemService.addPicture(picture);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                redirectAttributes.addFlashAttribute("success", "Photo has been added");
                return "redirect:/details/items/"+ item_id;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = "/cart")
    public String index(Model model,
                        RedirectAttributes redirectAttributes) {
        if (session.getAttribute("cart") == null) {
            List<Items> cart = new ArrayList<>();
            session.setAttribute("cart",cart);
            model.addAttribute("total", "0.0 KZT");
        }else {
            List<Items> cart = (List<Items>) session.getAttribute("cart");
            Double total = 0.0;
            for(Items it: cart){
                for (Integer i = 0; i < it.getQuantity(); i++){
                    total = total + it.getPrice();
                }
            }
            model.addAttribute("total", total);
        }
        redirectAttributes.addFlashAttribute("success","Item added to cart!");
        return "cart";
    }

    @GetMapping(value = "/buy/{id}")
    public String buy(@PathVariable("id") Long id,
                      RedirectAttributes redirectAttributes) {
        if (session.getAttribute("cart") == null) {
            List<Items> cart = new ArrayList<>();
            Items item = itemService.getItem(id);
            cart.add(item);
            session.setAttribute("cart", cart);
        } else {
            List<Items> cart = (List<Items>) session.getAttribute("cart");
            int index = this.exists(id, cart);
            if (index == -1) {
                Items item = itemService.getItem(id);
                item.setQuantity(1);
                cart.add(item);
            } else {
                    Integer quantity = cart.get(index).getQuantity() + 1;
                    cart.get(index).setQuantity(quantity);
            }
            session.setAttribute("cart", cart);
        }
        redirectAttributes.addFlashAttribute("success","Thank you for purchasing!");
        return "redirect:/cart";
    }

    @RequestMapping(value = "/clearCart", method = RequestMethod.GET)
    public String clearCart(RedirectAttributes redirectAttributes) {
        List<Items> cart = new ArrayList<>();
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("success","Cart is clear");
        return "redirect:/cart";
    }

    @RequestMapping(value = "/removeItemFromCart/{id}", method = RequestMethod.GET)
    public String remove(@PathVariable("id") Long id) {
        List<Items> cart = (List<Items>) session.getAttribute("cart");
        int index = this.exists(id, cart);
        cart.remove(index);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @RequestMapping(value = "/decreaseAmountFromCart/{id}", method = RequestMethod.GET)
    public String decrease(@PathVariable("id") Long id) {
        List<Items> cart = (List<Items>) session.getAttribute("cart");
        int index = this.exists(id, cart);
        if(cart.get(index).getQuantity()!=1){
            Integer quantity = cart.get(index).getQuantity();
            cart.get(index).setQuantity(quantity - 1);
        }else {
            cart.remove(index);
        }
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    private int exists(Long id, List<Items> cart) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getId()==id) {
                return i;
            }
        }
        return -1;
    }

    @PostMapping(value = "/payCart")
    public String payCart(Model model){
        List<Items> cart = (List<Items>) session.getAttribute("cart");
        Cart pay_cart = new Cart();
        pay_cart.setPaid_date(LocalDateTime.now());
        cartService.addCart(pay_cart);



        Long index = Long.valueOf(cartService.getCarts().size());

        for(Items it: cart){
            SoldItems soldItem = new SoldItems();
            soldItem.setItem(it);
            soldItem.setQuantity(it.getQuantity());
            soldItem.setCart(index);
            soldItemsService.addItem(soldItem);
        }

        return "redirect:/clearCart";
    }

    @GetMapping(value = "/cart/{id}")
    public String viewCart(Model model, @PathVariable(name = "id") Long id){
        List<SoldItems> cart = soldItemsService.getItemsByCart(id);
        List<Items> items = new ArrayList<>();

        for (SoldItems s: cart){
            Items item = new Items();
            item = s.getItem();
            item.setQuantity(s.getQuantity());
            items.add(item);
        }

        model.addAttribute("cart", items);
        return "soldItems";
    }

    @PostMapping(value = "/addComment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@RequestParam(name = "item_id") Long item_id,
                             @RequestParam(name = "comment") String comment_str){
        Items item = itemService.getItem(item_id);
        Users user = getUserData();

        Comments comment = new Comments();
        comment.setAddedDate(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setComment(comment_str);
        comment.setItem(item);

        commentService.addComment(comment);

        return "redirect:/details/" + item_id;
    }

    @PostMapping(value = "/editComment")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@RequestParam(name = "comment_id") Long comment_id,
                              @RequestParam(name = "comment") String comment_str){

        Comments comment = commentService.getCommentById(comment_id);

        comment.setComment(comment_str);

        commentService.saveComment(comment);

        return "redirect:/details/" + comment.getItem().getId();
    }

    @PostMapping(value = "/deleteComment")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@RequestParam(name = "comment_id") Long comment_id){
        Comments comment = commentService.getCommentById(comment_id);

        commentService.deleteComment(comment);

        return "redirect:/details/" + comment.getItem().getId();
    }
}
