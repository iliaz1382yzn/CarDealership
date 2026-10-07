import java.util.Objects;

public class Vehicle implements VehicleInterface, Comparable<Vehicle> {
    protected String brand;
    protected String model;
    protected double basePrice;
    protected int age;
    protected double km;
    protected FuelType fuelType;

    public Vehicle(String brand, String model, double basePrice, int age, double km, FuelType fuelType) {
        this.brand = brand;
        this.model = model;
        this.basePrice = basePrice;
        this.age = age;
        this.km = km;
        this.fuelType = fuelType;
    }

    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getBasePrice() { return basePrice; }
    public int getAge() { return age; }
    public double getKm() { return km; }
    public FuelType getFuelType() { return fuelType; }

    @Override
    public void applyDiscount(int percentage) {
        this.basePrice = this.basePrice * (1 - (percentage / 100.0));
    }

    @Override
    public int getFinalPrice() {
        double price = basePrice - (age * 150000);
        if (price < 200000) {
            price = 200000;
        }
        if (fuelType == FuelType.HYBRID) {
            price *= 1.15;
        } else if (fuelType == FuelType.DIESEL) {
            price *= 1.05;
        }
        return (int) price;
    }

    @Override
    public double averageKmPerYear() {
        if (age == 0) {
            return km;
        }
        return km / age;
    }

    @Override
    public String toString() {
        return String.format("Brand: %s %s | Fuel: %s | Age: %d years | KM: %.0f | Final Price: %d | Avg KM/Year: %.2f",
                brand, model, fuelType, age, km, getFinalPrice(), averageKmPerYear());
    }

    @Override
    public int compareTo(Vehicle other) {
        int priceCompare = Integer.compare(this.getFinalPrice(), other.getFinalPrice());
        if (priceCompare != 0) {
            return priceCompare;
        }
        return this.brand.compareTo(other.brand);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Double.compare(vehicle.basePrice, basePrice) == 0 &&
                age == vehicle.age &&
                Double.compare(vehicle.km, km) == 0 &&
                Objects.equals(brand, vehicle.brand) &&
                Objects.equals(model, vehicle.model) &&
                fuelType == vehicle.fuelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, basePrice, age, km, fuelType);
    }
}