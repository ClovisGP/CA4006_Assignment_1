package stuff;

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

    public Bookstore(ArrayList<Section> sectionList) {
            this.sectionList = sectionList;
    }

    /**
     * This function aims to generate a customer on a section every a ticks intervale
     */
    private void customerManagement() {
        if (this.lastTickCustomer < 10) {
            this.lastTickCustomer = this.scheduler.getTickNumber();

            Integer sectionIndex = (int)Math.random() * this.sectionList.size();
            if (sectionList.get(sectionIndex).takeBook()) {
                System.out.println("A book is buy from the section" + this.sectionList.get(sectionIndex).getName());
            }
        }
    }

    /**
     * This function aims to generate a delivery of some books every a ticks intervale
     */
    private void deliveryManagement() {
    }

    protected void doWork() {
        customerManagement();
        deliveryManagement();
    }
}
