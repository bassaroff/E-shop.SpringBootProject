package kz.springboot.SpringPookFinal.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "added_Date")
    private LocalDateTime addedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Items item;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users author;
}
