package SpringBoot.Codebase.domain.dto;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.springframework.beans.factory.annotation.Value;

@Measurement(name = "test_db")
public class Sensordto {

    @TimeColumn
    @Column(name ="humidity")
    private Double humidity;
    @Column(name = "temperature")
    private Double temperature;
    @Column(name = "cdc")
    private Double cdc;
    @Column(name = "farmname")
    private String farmName;

    public Double geTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    public Double getMoisture() {
        return moisture;
    }
    public void setMoisture(Double password) {
        this.moisture = moisture;
    }

    public void setHuminity(Double huminity){
        this.huminity = huminity;
    }
    public Double getHuminity(Double huminity){
        return huminity;
    }

    public String getFarmname() {
        return farmName;
    }
    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    @Override
    public String toString() {
        return "TestMeasurement {" +
                "farmName=" + farmName +
                ", Temperature=" + temperature +
                ", Huminity=" + huminity +
                ", Moisture=" + moisture +
                ",Temperature=" + temperature +

                '}';
    }

}
