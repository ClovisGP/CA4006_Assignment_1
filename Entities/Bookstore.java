package Entities;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import Objects.Book;
import Objects.Section;

import Tools.Logger;

/**
 * This class aims to manage the deliveries and the curstomers, and all.
 */
public class Bookstore extends SynchronizedThread {

    private ArrayList<Section> sectionList = null;
    private Integer lastTickCustomer = 0;
    private Integer lastTickDelivery = 0;
    private Integer clientSpawnRate;
    private Integer bowSpawnRate;
    private Integer boxSpawnSize;
    private Section deliveryArea;

    public Bookstore(ArrayList<Section> sectionList, Section deliveryArea, Integer clientSpawnRate, Integer bowSpawnRate, Integer boxSpawnSize) {
            this.sectionList = sectionList;
            this.clientSpawnRate = clientSpawnRate;
            this.bowSpawnRate = bowSpawnRate;
            this.boxSpawnSize = boxSpawnSize;
            this.deliveryArea = deliveryArea;
    }

    /**
     * This function aims to generate a customer on a section every a ticks intervale
     */
    private void customerManagement() {
        if ((this.lastTickCustomer + this.clientSpawnRate) <= this.scheduler.getTickNumber() + (int)(Math.random() * (this.clientSpawnRate / 3))) {
            this.lastTickCustomer = this.scheduler.getTickNumber();

            Integer sectionIndex = (int)(Math.random() * this.sectionList.size());
            if (sectionList.get(sectionIndex).takeBook() != null) {
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book is buy from the " + this.sectionList.get(sectionIndex).getName() + " sections.");
            }
        }
    }

    /**
     * This function aims to generate a delivery of some books every a ticks intervale
     */
    private void deliveryManagement() {
        if ((this.lastTickDelivery + this.bowSpawnRate) <= (this.scheduler.getTickNumber() + Math.random() * (this.bowSpawnRate / 10))) {

            this.lastTickDelivery = this.scheduler.getTickNumber();
            for (int comp = 0; comp < this.boxSpawnSize; comp++) {
                this.deliveryArea.addBook(new Book(sectionList.get((int)(Math.random() * this.sectionList.size())).getName()));
            }
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A new delivery was made. The delivery area contains now " + (this.deliveryArea.getNbCurrentBook() < 50 ? this.deliveryArea.getNbCurrentBook() : this.deliveryArea.getNbCurrentBook() + ". You should consider hiring some news assistants sir") + ".");
        }
    }

    /**
     * This function aims to generate the first delivery
     */
    public static void firstDelivery(Section deliveryArea, int boxSpawnSize, ArrayList<Section> sectionList) {
        // deliveryArea already contains a first book
        for (int comp = 0; comp < (boxSpawnSize - 1); comp++) {
            deliveryArea.addBook(new Book(sectionList.get((int)(Math.random() * sectionList.size())).getName()));
        }
        Logger.writeLog("T = Before openning | A new delivery was made. The delivery area contains now " + deliveryArea.getNbCurrentBook() + ".");
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();
    }
}
