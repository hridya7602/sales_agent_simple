package Salesllmproject.salesllmproject.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`lead`")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String contactName;
    private String email;
    private String industry;
    private Integer companySize;
    private String notes;
    private Double score;

    // UNQUALIFIED, QUALIFIED, WON, LOST
    private String status;

    private LocalDate lastContactDate;
}
