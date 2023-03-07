package stuff;

import Entities.Book;
import Entities.Section;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

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
                System.out.println("A book is buy from the section" + this.sectionList.get(sectionIndex).getName());
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

            System.out.println("A new delivery was made");
            System.out.println("nb book delivered => " + this.deliveryArea.getNbCurrentBook() + " At the ticks number => " + this.scheduler.getTickNumber());
        }
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();
        int timeToWaste = (int)(Math.random() * 900 + 100);
        try {
            TimeUnit.MILLISECONDS.sleep(timeToWaste);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
