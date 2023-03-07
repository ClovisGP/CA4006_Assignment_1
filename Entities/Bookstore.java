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
        if ((this.lastTickCustomer + this.clientSpawnRate) <= this.scheduler.getTickNumber()) {
            this.lastTickCustomer = this.scheduler.getTickNumber();

            Integer sectionIndex = (int)Math.random() * this.sectionList.size();
            if (sectionList.get(sectionIndex).takeBook() != null) {
                Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A book is buy from the section" + this.sectionList.get(sectionIndex).getName());
            }
        }
    }

    /**
     * This function aims to generate a delivery of some books every a ticks intervale
     */
    private void deliveryManagement() {
        if ((this.lastTickDelivery + this.bowSpawnRate) <= this.scheduler.getTickNumber()) { //to recode for a random because on 100, I don't understand how to do it

            this.lastTickDelivery = this.scheduler.getTickNumber();
            for (int comp = 0; comp < this.boxSpawnSize; comp++) {
                this.deliveryArea.addBook(new Book(sectionList.get((int)Math.random() * this.sectionList.size()).getName()));
            }
            Logger.writeLog("T = " + this.scheduler.getTickNumber() + " | A new delivery was made. The delivery area contains now " + (this.deliveryArea.getNbCurrentBook() < 50 ? this.deliveryArea.getNbCurrentBook() : this.deliveryArea.getNbCurrentBook() + ". You should consider hiring some news assistants sir") + ".");
        }
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();

        // To remove
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
