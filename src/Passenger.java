public class Passenger implements java.io.Serializable
{
    //object attributes
    private String customerName;
    private String customerId;
    private String bookedSeat;
    private int secondsInQueue;


    //getter and the setter for the customer name
    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }


    //getter and the setter for the customer id
    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }


    //getter and setter for the booked seat
    public String getBookedSeat()
    {
        return bookedSeat;
    }

    public void setBookedSeat(String bookedSeat)
    {
        this.bookedSeat = bookedSeat;
    }


    //setter and getter for the seconds in queue
    public void setSecondsInQueue(int seconds)
    {
        this.secondsInQueue= seconds;
    }

    public int getSecondsInQueue()
    {
        return secondsInQueue;
    }

}
