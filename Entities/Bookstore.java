package Entities;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import Objects.Book;
import Objects.Section;

import Tools.Logger;
import Tools.StatsManager;

/**
 * This class aims to manage the deliveries and the curstomers, and all.
 */
public class Bookstore extends SynchronizedThread {

    private ArrayList<Section> sectionList = null;
    private Integer lastTickCustomer = 0;
    private Integer lastTickDelivery = 0;
    private Integer clientSpawnRate;
    private Integer boxSpawnRate;
    private Integer boxSpawnSize;
    private Section deliveryArea;
    private StatsManager statsManager;

    public Bookstore(ArrayList<Section> sectionList, Section deliveryArea, Integer clientSpawnRate, Integer boxSpawnRate, Integer boxSpawnSize, StatsManager statsManager) {
            this.sectionList = sectionList;
            this.clientSpawnRate = clientSpawnRate;
            this.boxSpawnRate = boxSpawnRate;
            this.boxSpawnSize = boxSpawnSize;
            this.deliveryArea = deliveryArea;
            this.statsManager = statsManager;
    }

    /**
     * Tell if the resssource must spawn
     * @param averageSpawnRateInterval - the spawn rate of the ressource
     * @return boolean - True if the ressource must spawn
     */
    private boolean doesItSpawn(Integer averageSpawnRateInterval) {
        if (averageSpawnRateInterval == 0 || averageSpawnRateInterval == 1) return true;
        if (Math.random() < (1 / (double)averageSpawnRateInterval)) return true;
        return false;
    }

    /**
     * This function aims to generate a customer on a section every a ticks intervale
     */
    private void customerManagement() {
        if (doesItSpawn(this.clientSpawnRate)) {
            this.lastTickCustomer = this.scheduler.getTickNumber();

            Integer sectionIndex = (int)(Math.random() * this.sectionList.size());
            if (sectionList.get(sectionIndex).takeBook() != null) {
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book has been bought from the " + this.sectionList.get(sectionIndex).getName() + " section.");
            }
        }
    }

    /**
     * This function aims to generate a delivery of some books every a ticks intervale
     */
    private void deliveryManagement() {
        if (doesItSpawn(this.boxSpawnRate)) {
            this.lastTickDelivery = this.scheduler.getTickNumber();
            for (int comp = 0; comp < this.boxSpawnSize; comp++) {
                this.deliveryArea.addBook(new Book(sectionList.get((int)(Math.random() * this.sectionList.size())).getName()));
            }
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A new delivery was made. The delivery area contains now " + (this.deliveryArea.getNbCurrentBook() < 50 ? this.deliveryArea.getNbCurrentBook() : this.deliveryArea.getNbCurrentBook() + ". You should consider hiring some new assistants sir") + ".");
        }
    }

    /**
     * This function aims to generate the first delivery
     */
    public static void firstDelivery(Section deliveryArea, int boxSpawnSize, ArrayList<Section> sectionList) {
        // deliveryArea already contains a first book
        for (int comp = 0; comp < boxSpawnSize; comp++) {
            deliveryArea.addBook(new Book(sectionList.get((int)(Math.random() * sectionList.size())).getName()));
        }
        Logger.writeLog("T = Before openning | A new delivery was made. The delivery area contains now " + deliveryArea.getNbCurrentBook() + ".");
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();
    }
}
