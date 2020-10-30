import java.util.ArrayList;
import java.util.Scanner;

public class PassengerQueue
{
    private Passenger[] passengerToBadulla = new Passenger[21];
    private Passenger[] passengerToColombo = new Passenger[21];

    private int firstToColombo;
    private int lastToColombo;
    private int maxStayInQueueToBadulla;
    private int minStayInQueueToBadulla;
    private int maxLengthToBadulla;

    private int firstToBadulla;
    private int lastToBadulla;
    private int maxStayInQueueToColombo;
    private int minStayInQueueToColombo;
    private int maxLengthToColombo;

    //setter to add passengers to badulla route
    public void addToBadulla(Passenger customer)
    {
        passengerToBadulla[lastToBadulla] = customer;
        lastToBadulla=(lastToBadulla+1)%21;
        maxLengthToBadulla++;
    }

    //delete the object to badulla
    public void deleteToBadulla(String customerId)
    {
        //boolean value for display message to the user object is not available
        boolean valueForCheckAvailability = false;

        for(int i =0 ; i< passengerToBadulla.length;i++)
        {
            if (passengerToBadulla[i] == null)
            {
                continue;
            }
            else if (passengerToBadulla[i].getCustomerId().equals(customerId))
            {
                //trues the boolean varaible
                valueForCheckAvailability=true;

                //displays the information
                System.out.println("To the Entered N.I.C Number Stored Details are: ");
                System.out.println("Name: " + passengerToBadulla[i].getCustomerName());
                System.out.println("Seat: " + passengerToBadulla[i].getBookedSeat());
                System.out.println();

                //loop for verify data and delete
                while (true)
                {
                    System.out.println("To Delete the Above Passenger from the Train Queue Enter Y or to cancel Enter N.");
                    System.out.print("Choice: ");
                    Scanner scannerUserChoice = new Scanner(System.in);
                    String userChoice = scannerUserChoice.nextLine();
                    userChoice = userChoice.toUpperCase();

                    if (userChoice.equals("Y"))
                    {
                        maxLengthToBadulla--;
                        passengerToBadulla[i] = null;
                        System.out.println("\nPassenger Deleted Successfully");
                        break;
                    }
                    else if (userChoice.equals("N"))
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid Input. Please Enter Again\n");
                    }
                }
            }
        }

        //when object is not available or wrong displays this
        if(!(valueForCheckAvailability))
        {
            System.out.println("Entered N.I.C Number is Incorrect or No Data to Given N.I.C Number.\n");
        }
    }

    //method for reorder the queue by setting the null values to the corner
    public void reOrderToBadulla()
    {
        Passenger tempValue;
        for(int j=0;j<(passengerToBadulla.length-1);j++)
        {
            for (int i = 0; i < (passengerToBadulla.length - 1); i++)
            {
                //swapping null values to right corner
                if (passengerToBadulla[i] == null)
                {
                    tempValue = passengerToBadulla[i + 1];
                    passengerToBadulla[i + 1] = passengerToBadulla[i];
                    passengerToBadulla[i] = tempValue;
                }
            }
        }

        //setting the last int and size of the queue after reordering
        for(int k=0;k<passengerToBadulla.length-1;k++)
        {
            if(!(passengerToBadulla[k]==null))
            {
                lastToBadulla=k+1;
                maxLengthToBadulla=k+1;
            }
        }

        //checks the total list null or not
        int count =0;
        for(int k=0;k<passengerToBadulla.length;k++)
        {
            if(passengerToBadulla[k]==null)
            {
                count++;
            }
        }

        //if the total list is null set the first,last and size =0
        if(passengerToBadulla.length==count)
        {
            firstToBadulla=0;
            lastToBadulla=0;
            maxLengthToBadulla=0;
        }
    }


    //getter for the passenger queue
    public ArrayList getObjectAvailableToBadulla()
    {
        firstToBadulla=0;
        ArrayList queueList = new ArrayList();
        for (int i = 0; i < getMaxLengthToBadulla(); i++)
        {
            queueList.add(passengerToBadulla[firstToBadulla+i]);
        }
        return queueList;
    }


    //method for set null for all values in the queue
    public  void setNullToBadulla()
    {
        for(int i=0;i<maxLengthToBadulla;i++)
        {
            passengerToBadulla[i]=null;
        }
    }

    //method for set first,last and size=0
    public void setFirstLastMaxToBadulla()
    {
        firstToBadulla=0;
        lastToBadulla=0;
        maxLengthToBadulla=0;
    }

    //getter for max length
    public int getMaxLengthToBadulla(){
        return maxLengthToBadulla;
    }

    //method of check the queue is empty or not
    public boolean isEmptyToBadulla(){
        return getMaxLengthToBadulla()==0;
    }

    //method for check the queue is full or not
    public boolean isFullToBadulla(){
        return getMaxLengthToBadulla()==21;
    }

    //getter for values in the queue
    public Passenger getQueueValueToBadulla()
    {
        Passenger object =passengerToBadulla[firstToBadulla];
        passengerToBadulla[firstToBadulla]=null;
        firstToBadulla++;
        return object;
    }

    //set max and min times for calculations
    public void setTimeToBadulla()
    {
        maxStayInQueueToBadulla=0;
        minStayInQueueToBadulla=400;
    }

    //setter for the max stay in queue
    public void setMaxStayInQueueToBadulla(int value)
    {
        if(maxStayInQueueToBadulla<value)
        {
            maxStayInQueueToBadulla=value;
        }
    }

    //getter for max stay in queue
    public int getMaxStayInQueueToBadulla(){
        return maxStayInQueueToBadulla;
    }

    //setter for min stay in queue
    public void setMinStayInQueueToBadulla(int value)
    {
        if(minStayInQueueToBadulla>value)
        {
            minStayInQueueToBadulla=value;
        }
    }

    //getter for min stay in queue
    public int getMinStayInQueueToBadulla()
    {
        return minStayInQueueToBadulla;
    }



    //setter to add passengers to colombo route
    public void addToColombo(Passenger customer)
    {
        passengerToColombo[lastToColombo] = customer;
        lastToColombo=(lastToColombo+1)%21;
        maxLengthToColombo++;
    }

    //delete the object to colombo
    public void deleteToColombo(String customerId)
    {
        //boolean value for display message to the user object is not available
        boolean valueForCheckAvailability = false;

        for(int i =0 ; i< passengerToColombo.length;i++)
        {
            if (passengerToColombo[i] == null)
            {
                continue;
            }
            else if (passengerToColombo[i].getCustomerId().equals(customerId))
            {
                //trues the boolean varaible
                valueForCheckAvailability=true;

                //displays the information
                System.out.println("To the Entered N.I.C Number Stored Details are: ");
                System.out.println("Name: " + passengerToColombo[i].getCustomerName());
                System.out.println("Seat: " + passengerToColombo[i].getBookedSeat());
                System.out.println();

                //loop for verify data and delete
                while (true)
                {
                    System.out.println("To Delete the Above Passenger from the Train Queue Enter Y or to cancel Enter N.");
                    System.out.print("Choice: ");
                    Scanner scannerUserChoice = new Scanner(System.in);
                    String userChoice = scannerUserChoice.nextLine();
                    userChoice = userChoice.toUpperCase();

                    if (userChoice.equals("Y"))
                    {
                        maxLengthToColombo--;
                        passengerToColombo[i] = null;
                        System.out.println();
                        System.out.println("\nPassenger Deleted Succesfully");
                        break;
                    }
                    else if (userChoice.equals("N"))
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid Input. Please Enter Again\n");
                    }
                }
            }
        }

        //when object is not available or wrong displays this
        if(!(valueForCheckAvailability))
        {
            System.out.println("Entered N.I.C Number is incorrect or No Data to Given N.I.C Number.\n");
        }
    }

    //method for reorder the queue by setting the null values to the corner
    public void reOrderToColombo()
    {
        Passenger tempValue;
        for(int j=0;j<(passengerToColombo.length-1);j++)
        {
            for (int i = 0; i < (passengerToColombo.length - 1); i++)
            {
                //swapping null values to right corner
                if (passengerToColombo[i] == null)
                {
                    tempValue = passengerToColombo[i + 1];
                    passengerToColombo[i + 1] = passengerToColombo[i];
                    passengerToColombo[i] = tempValue;
                }
            }
        }

        //setting the last int and size of the queue after reordering
        for(int k=0;k<passengerToColombo.length-1;k++)
        {
            if(!(passengerToColombo[k]==null))
            {
                lastToColombo=k+1;
                maxLengthToColombo=k+1;
            }
        }


        //checks the total list null or not
        int count =0;
        for(int k=0;k<passengerToColombo.length;k++)
        {
            if(passengerToColombo[k]==null)
            {
                count++;
            }
        }

        //if the total list is null set the first,last and size =0
        if(passengerToColombo.length==count)
        {
            firstToColombo=0;
            lastToColombo=0;
            maxLengthToColombo=0;
        }
    }


    //getter for the passenger queue
    public ArrayList getObjectAvailableToColombo()
    {
        firstToColombo = 0;
        ArrayList queueList = new ArrayList();
        if (!(isEmptyToColombo()))
        {
            for (int i = 0; i < maxLengthToColombo; i++)
            {
                queueList.add(passengerToColombo[firstToColombo+i]);
            }
        }
        return queueList;
    }

    //method for set null for all values in the queue
    public  void setNullToColombo()
    {
        for(int i=0;i<maxLengthToColombo;i++)
        {
            passengerToColombo[i]=null;
        }
    }

    //method for set first,last and size=0
    public void setFirstLastMaxToColombo()
    {
        firstToColombo=0;
        lastToColombo=0;
        maxLengthToColombo=0;
    }


    //getter for max length
    public int getMaxLengthToColombo(){
        return maxLengthToColombo;
    }

    //method of check the queue is empty or not
    public boolean isEmptyToColombo(){
        return getMaxLengthToColombo()==0;
    }

    //method for check the queue is full or not
    public boolean isFullToColombo(){
        return getMaxLengthToColombo()==21;
    }

    //getter for values in the queue
    public Passenger getQueueValueToColombo()
    {
        Passenger object =passengerToColombo[firstToColombo];
        passengerToColombo[firstToColombo]=null;
        firstToColombo++;
        return object;
    }

    //set max and min times for calculations
    public void setTimeToColombo()
    {
        maxStayInQueueToColombo=0;
        minStayInQueueToColombo=400;
    }

    //setter for the max stay in queue
    public void setMaxStayInQueueToColombo(int value){
        if(maxStayInQueueToColombo<value){
            maxStayInQueueToColombo=value;
        }
    }

    //getter for max stay in queue
    public int getMaxStayInQueueToColombo()
    {
        return maxStayInQueueToColombo;
    }

    //setter for min stay in queue
    public void setMinStayInQueueToColombo(int value)
    {
        if(minStayInQueueToColombo>value)
        {
            minStayInQueueToColombo=value;
        }
    }

    //getter for min stay in queue
    public int getMinStayInQueueToColombo(){
        return minStayInQueueToColombo;
    }

}
