public class ElectricVehicle extends Vehicle implements ElectricInterface {
    private int batteryCapacity;
    private boolean hasHeatPump;

    public ElectricVehicle(String brand, String model, double basePrice, int age, double km, FuelType fuelType, int batteryCapacity, boolean hasHeatPump) {
        super(brand, model, basePrice, age, km, fuelType);
        this.batteryCapacity = batteryCapacity;
        this.hasHeatPump = hasHeatPump;
    }

    public int getBatteryCapacity() { return batteryCapacity; }
    public boolean isHasHeatPump() { return hasHeatPump; }

    @Override
    public int getFinalPrice() {
        double price = super.getFinalPrice();
        if (batteryCapacity >= 60) {
            price *= 1.10;
        }
        if (hasHeatPump) {
            price *= 1.05;
        }
        return (int) price;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Battery: %d kWh | Heat Pump: %s", batteryCapacity, hasHeatPump ? "Yes" : "No");
    }

    @Override
    public boolean hasSameRangeCategory(Vehicle other) {
        if (other instanceof ElectricVehicle) {
            ElectricVehicle ev = (ElectricVehicle) other;
            return Math.abs(this.batteryCapacity - ev.batteryCapacity) <= 5;
        }
        return false;
    }

    @Override
    public int batteryCostShare() {
        return batteryCapacity * 25000;
    }
}