package com.example.peekaroundcorner.navigation;
/*
 * Calculate Coordinate from sensor data
 * Using small period of time to calculate current speed and current location
 * Time period 20ms
 */
public class Location {

	/*
	 * Initial location data
	 */
	public double coordinate_x;
	public double coordinate_y;
	public double speed_x=0;
	public double speed_y=0;
	private double orientation;
	public double acceleration_x=0;
	public double acceleration_y=0;
	private double time=20;
	public Location(double x,double y){
		/*
		 * Constructor 
		 * Start from special coordinates
		 */
		coordinate_x=x;
		coordinate_y=y;
	}
	public void updateAcceleration(double x,double z,double oren){
		/*
		 * Transfer sensor data into x-y coordinate
		 */
		orientation=oren;
		acceleration_x=x*Math.sin((orientation+90)*Math.PI/180)-z*Math.sin(orientation*Math.PI/180);
		acceleration_y=x*Math.cos((orientation+90)*Math.PI/180)-z*Math.cos(orientation*Math.PI/180);
	}
	public void updateSpeed(){
		/*
		 * Update x and y speed
		 * xspeed=xacceleration*time
		 * yspeed=yacceleration*time
		 */
		speed_x=speed_x+(time/1000)*acceleration_x;
		speed_y=speed_y+(time/1000)*acceleration_y;
	}
	public void updateCoordinate(){
		/*
		 * Update x-y coordinate
		 * Similar with speed
		 */
		coordinate_x=coordinate_x+(time/1000)*speed_x;
		coordinate_y=coordinate_y+(time/1000)*speed_y;
	}
}
