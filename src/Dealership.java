import java.io.*;
import java.util.*;

public class Dealership {
    private TreeSet<Vehicle> inventory;

    public Dealership() {
        this.inventory = new TreeSet<>();
    }

    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File not found! Loading hardcoded data...");
            loadHardcodedData();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("#");
                if (parts[0].equals("VEHICLE")) {
                    String brandModel = parts[1];
                    String[] bm = brandModel.split(" ", 2);
                    String brand = bm[0];
                    String model = bm.length > 1 ? bm[1] : "";
                    double basePrice = Double.parseDouble(parts[2]);
                    int age = Integer.parseInt(parts[3]);
                    double km = Double.parseDouble(parts[4]);
                    FuelType fuelType = FuelType.valueOf(parts[5]);

                    inventory.add(new Vehicle(brand, model, basePrice, age, km, fuelType));

                } else if (parts[0].equals("ELECTRIC")) {
                    String brandModel = parts[1];
                    String[] bm = brandModel.split(" ", 2);
                    String brand = bm[0];
                    String model = bm.length > 1 ? bm[1] : "";
                    double basePrice = Double.parseDouble(parts[2]);
                    int age = Integer.parseInt(parts[3]);
                    double km = Double.parseDouble(parts[4]);
                    FuelType fuelType = FuelType.valueOf(parts[5]);
                    int batteryCapacity = Integer.parseInt(parts[6]);
                    boolean hasHeatPump = parts[7].equalsIgnoreCase("yes");

                    inventory.add(new ElectricVehicle(brand, model, basePrice, age, km, fuelType, batteryCapacity, hasHeatPump));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.out.println("Loading hardcoded data instead...");
            inventory.clear();
            loadHardcodedData();
        }
    }

    private void loadHardcodedData() {
        inventory.add(new Vehicle("Toyota", "Corolla", 5500000, 4, 75000, FuelType.HYBRID));
        inventory.add(new Vehicle("Volkswagen", "Golf", 4200000, 6, 120000, FuelType.DIESEL));
        inventory.add(new Vehicle("Ford", "Focus", 3100000, 8, 145000, FuelType.PETROL));
        inventory.add(new Vehicle("BMW", "320d", 7800000, 3, 60000, FuelType.DIESEL));
        inventory.add(new Vehicle("Opel", "Astra", 2200000, 11, 190000, FuelType.PETROL));
        inventory.add(new ElectricVehicle("Nissan", "Leaf", 4800000, 5, 65000, FuelType.HYBRID, 40, false));
        inventory.add(new ElectricVehicle("Tesla", "Model 3", 11500000, 2, 38000, FuelType.HYBRID, 75, true));
        inventory.add(new ElectricVehicle("Renault", "Zoe", 3900000, 6, 82000, FuelType.HYBRID, 52, true));
        inventory.add(new ElectricVehicle("Kia", "EV6", 13200000, 1, 15000, FuelType.HYBRID, 77, true));
        inventory.add(new ElectricVehicle("VW", "ID.3", 7100000, 4, 54000, FuelType.HYBRID, 58, false));
    }

    public void processAndWriteOutput() {
        StringBuilder output = new StringBuilder();

        double totalKm = 0;
        for (Vehicle v : inventory) {
            totalKm += v.getKm();
        }
        double avgKm = inventory.isEmpty() ? 0 : totalKm / inventory.size();
        output.append("1. Fleet Average Mileage: ").append(String.format("%.2f", avgKm)).append(" km\n");

        Vehicle cheapest = inventory.first();
        output.append("2. Cheapest Vehicle:\n   ").append(cheapest.toString()).append("\n");

        ElectricVehicle topEv = null;
        for (Vehicle v : inventory) {
            if (v instanceof ElectricVehicle) {
                if (topEv == null || v.getFinalPrice() > topEv.getFinalPrice()) {
                    topEv = (ElectricVehicle) v;
                }
            }
        }
        if (topEv != null) {
            output.append("3. Top Electric Vehicle (Highest Price):\n");
            output.append("   Model: ").append(topEv.getBrand()).append(" ").append(topEv.getModel()).append("\n");
            output.append("   Battery Capacity: ").append(topEv.getBatteryCapacity()).append(" kWh\n");
            output.append("   Avg KM/Year: ").append(String.format("%.2f", topEv.averageKmPerYear())).append("\n");
        } else {
            output.append("3. Top Electric Vehicle: No electric vehicles in inventory.\n");
        }

        long totalValue = 0;
        for (Vehicle v : inventory) {
            totalValue += v.getFinalPrice();
        }
        output.append("4. Total Fleet Inventory Value: ").append(totalValue).append("\n");

        double totalFinalPrice = 0;
        for (Vehicle v : inventory) {
            totalFinalPrice += v.getFinalPrice();
        }
        double avgFinalPrice = inventory.isEmpty() ? 0 : totalFinalPrice / inventory.size();

        output.append("5. Filtered Hybrid Inventory (Price <= ").append(String.format("%.0f", avgFinalPrice)).append("):\n");
        boolean foundHybrid = false;
        for (Vehicle v : inventory) {
            if (v.getFuelType() == FuelType.HYBRID && v.getFinalPrice() <= avgFinalPrice) {
                output.append("   ").append(v.toString()).append("\n");
                foundHybrid = true;
            }
        }
        if (!foundHybrid) {
            output.append("   No hybrid vehicles match the criteria.\n");
        }

        int youngCount = 0;
        for (Vehicle v : inventory) {
            if (v.getAge() < 5) {
                youngCount++;
            }
        }
        output.append("6. Young Inventory Count (Age < 5): ").append(youngCount).append("\n");

        output.append("7. Discount Simulation (10% off Cheapest Vehicle):\n");
        if (!inventory.isEmpty()) {
            Vehicle testVehicle = inventory.first();
            int oldPrice = testVehicle.getFinalPrice();
            testVehicle.applyDiscount(10);
            output.append("   Vehicle: ").append(testVehicle.getBrand()).append(" ").append(testVehicle.getModel()).append("\n");
            output.append("   Old Final Price: ").append(oldPrice).append("\n");
            output.append("   New Final Price after 10% discount: ").append(testVehicle.getFinalPrice()).append("\n");
        }

        System.out.println(output.toString());

        try (PrintWriter writer = new PrintWriter(new FileWriter("outputDealership.txt"))) {
            writer.print(output.toString());
            System.out.println(">> Output successfully written to outputDealership.txt");
        } catch (IOException e) {
            System.out.println("Error writing to output file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Dealership dealership = new Dealership();
        dealership.loadFromFile("vehicles.txt");
        dealership.processAndWriteOutput();
    }
}