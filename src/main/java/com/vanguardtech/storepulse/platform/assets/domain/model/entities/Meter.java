package com.vanguardtech.storepulse.platform.assets.domain.model.entities;


import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.MeterType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "meters")
@Getter
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeterType type;

    @Column(nullable = false)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    protected Meter() {}

    public Meter(MeterType type){
        if (type == null)
            throw new IllegalArgumentException("Meter type is required");
        this.type = type;
        this.unit = type == MeterType.ELECTRICITY ? "kWh" : "m³";
        this.status = AssetStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AssetStatus.ACTIVE;
    }
}
