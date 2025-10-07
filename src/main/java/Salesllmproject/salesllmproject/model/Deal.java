package Salesllmproject.salesllmproject.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String dealOwner;
    private String stage;
    private Double amount;
    private String notes;

    @Override
    public String toString() {
        return "Deal{" +
                "companyName='" + companyName + '\'' +
                ", dealOwner='" + dealOwner + '\'' +
                ", stage='" + stage + '\'' +
                ", amount=" + amount +
                ", notes='" + notes + '\'' +
                '}';
    }
}


