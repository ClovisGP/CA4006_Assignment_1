package Entities;

import java.util.ArrayList;
import Objects.Section;
import Objects.Delivery;
import Tools.Logger;
import Tools.StatsManager;

/**
 * This class aims to manage the deliveries and the curstomers, and all.
 */
public class Bookstore extends SynchronizedThread {

    private ArrayList<Section> sectionList = null;
    private Integer clientSpawnRate;
    private Integer boxSpawnRate;
    private Delivery deliveryArea;
    private StatsManager statsManager;

    public Bookstore(ArrayList<Section> sectionList, Delivery deliveryArea, Integer clientSpawnRate, Integer boxSpawnRate) {
            this.sectionList = sectionList;
            this.clientSpawnRate = clientSpawnRate;
            this.boxSpawnRate = boxSpawnRate;
            this.deliveryArea = deliveryArea;
            this.statsManager = StatsManager.getInstance();
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

            Integer sectionIndex = (int)(Math.random() * this.sectionList.size());
            if (sectionIndex >= this.sectionList.size()) sectionIndex = this.sectionList.size() - 1;
            if (sectionList.get(sectionIndex).takeBook() != null) {
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book has been bought from the " + this.sectionList.get(sectionIndex).getName() + " section.");
            }
        }
    }

    public int getbooksInSection() {
        int res = 0;
        for (Section section : this.sectionList) {
            res += section.getNbCurrentBook();
        }
        return res;
    }

    public int getBooksInDelivery() {
        return deliveryArea.getNbCurrentBook();
    }

    public int getNumberClientsWaiting() {
        int res = 0;
        for (Section section : this.sectionList) {
            res += section.getNbWaitingCustomer();
        }
        return res;
    }

    /**
     * This function aims to generate a delivery of some books every a ticks intervale
     */
    private void deliveryManagement() {
        if (doesItSpawn(this.boxSpawnRate)) {
            deliveryArea.doADelivery();
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A new delivery was made. The delivery area contains now " + (this.deliveryArea.getNbCurrentBook() < 50 ? Integer.toString(this.deliveryArea.getNbCurrentBook()) + " book(s)" : this.deliveryArea.getNbCurrentBook() + " books. You should consider hiring some new assistants sir") + ".");
        }
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();
    }
}
