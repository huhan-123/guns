package com.stylefeng.guns.rest.modular.order;

/**
 * @Author: huhan
 * @Date 2020/9/2 11:16 下午
 * @Description
 * @Verion 1.0
 */
public class test {
    public static void main(String[] args) {
        String soldSeats = "1,2,3,4,5,6,9";
        String seats = "10,9";
        if (soldSeats != null && !soldSeats.isEmpty()) {
            String[] soldSeatArray = soldSeats.split(",");
            for (String seat : seats.split(",")) {
                for (String soldSeat : soldSeatArray) {
                    if (seat.equals(soldSeat)) {
                        System.out.println(true);
                        return;
                    }
                }
            }
            System.out.println(false);
            return;
        }
    }
}
