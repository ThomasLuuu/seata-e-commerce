package app.thomas.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "s_shippings")
public class ShippingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "COST", precision = 15, scale = 2)
    private BigDecimal cost;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "STATUS")
    private String status; // For TCC: PENDING, CONFIRMED, CANCELED

    @Column(name = "XID")
    private String xid; // For TCC fence

    @Column(name = "BRANCH_ID")
    private Long branchId; // For TCC fence
}