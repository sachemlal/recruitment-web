package fr.d2factory.libraryapp.member;

public class Resident extends Member {

    static final int maxPeriod = 60;
    static final float rentPricePerDay = (float) 0.1;
    static final float rentPricePerLateDay = (float) 0.2;

    public Resident(String firstName, String lastName, float wallet) {
        super(firstName, lastName, wallet);
        this.setMaxPeriod(Resident.maxPeriod);
        this.setRentPricePerDay(Resident.rentPricePerDay);
        this.setRentPricePerLateDay(Resident.rentPricePerLateDay);
    }

}
