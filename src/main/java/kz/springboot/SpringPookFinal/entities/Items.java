package kz.springboot.SpringPookFinal.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Items {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "small_pic_url")
    private String smallPicURL;

    @Column(name = "large_pic_url")
    private String largePicURL;

    @Column(name = "added_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime addedTime = LocalDateTime.now();

    @Column(name = "in_top_page", columnDefinition = "boolean default false")
    private Boolean inTopPage = Boolean.FALSE;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    Brands brand;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Categories> categories;
}
