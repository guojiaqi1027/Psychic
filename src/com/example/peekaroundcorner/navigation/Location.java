package com.example.peekaroundcorner.navigation;

public class Location {

	public double coordinate_x;
	public double coordinate_y;
	public double speed_x=0;
	public double speed_y=0;
	private double orientation;
	public double acceleration_x=0;
	public double acceleration_y=0;
	private double time=100;
	public Location(double x,double y){
		coordinate_x=x;
		coordinate_y=y;
	}
	public void updateAcceleration(double x,double z,double oren){
		orientation=oren;
		acceleration_x=x*Math.sin((oren+90)*Math.PI/180)-z*Math.sin(oren*Math.PI/180);
		acceleration_y=x*Math.cos((oren+90)*Math.PI/180)-z*Math.cos(oren*Math.PI/180);
	}
	public void updateSpeed(){
		speed_x=speed_x+(time/1000)*acceleration_x;
		speed_y=speed_y+(time/1000)*acceleration_y;
	}
	public void updateCoordinate(){
		coordinate_x=coordinate_x+(time/1000)*speed_x;
		coordinate_y=coordinate_y+(time/1000)*speed_y;
	}
}
