package model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(name = "order_number")
    private String orderNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_rows",
                    joinColumns = @JoinColumn(name = "orders_id",
                                            referencedColumnName = "id")
    )
    @Valid
    private List<OrderRow> orderRows;


//    public void add(OrderRow orderRow) {
//        if (orderRows == null) {
//            orderRows = new ArrayList<>();
//        }
//        orderRows.add(orderRow);
//    }
}
