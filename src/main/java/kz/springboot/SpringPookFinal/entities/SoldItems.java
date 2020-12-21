package kz.springboot.SpringPookFinal.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "sold_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SoldItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private Items item;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "cart")
    private Long cart;
}
