package fr.d2factory.libraryapp.member;

public class Student extends Member {

    /**
     * Level of study for a student
     */
    int yearOfStudy;
    static final int firstYearFreePeriod = 15;
    static final int maxPeriod = 30;
    static final float rentPricePerDay = (float) 0.1;
    static final float rentPricePerLateDay = (float) 0.15;

    public Student(String firstName, String lastName, float wallet, int yearOfStudy) {
        super(firstName, lastName, wallet);
        this.yearOfStudy = yearOfStudy;

        if(this.yearOfStudy == 1) {
            this.setFreePeriod(Student.firstYearFreePeriod);
        }

        this.setMaxPeriod(Student.maxPeriod);
        this.setRentPricePerDay(Student.rentPricePerDay);
        this.setRentPricePerLateDay(Student.rentPricePerLateDay);
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

}
