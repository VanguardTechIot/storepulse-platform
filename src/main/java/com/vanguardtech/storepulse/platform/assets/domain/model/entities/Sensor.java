package com.vanguardtech.storepulse.platform.assets.domain.model.entities;


import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.AssetStatus;
import com.vanguardtech.storepulse.platform.assets.domain.model.valueobjects.SensorType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "sensors")
@Getter
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType type;

    /** The unit of its readings, for example, "°C"; empty for sensors without a numeric reading. */
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetStatus status;

    protected Sensor() {}

    public Sensor(SensorType type, String unit){
        if (type == null)
            throw new IllegalArgumentException("Sensor type is required");
        this.type = type;
        this.unit = unit;
        this.status = AssetStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == AssetStatus.ACTIVE;
    }
}
