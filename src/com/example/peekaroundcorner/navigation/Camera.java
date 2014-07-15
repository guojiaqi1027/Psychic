/**
 * Build up the information of camera network
 * Using ID and URL to identify and catch the video stream
 **/
package com.example.peekaroundcorner.navigation;
public class Camera {

	private String cameraID;
	private String cameraURL;
	public String getId(){
		/**
		 * Get the ID of camera
		 * ID will be showed in QR code
		 */
		return cameraID;
	}
	public String getURL(){
		/**
		 * Get the URL of camera
		 * Assigned by PC manager
		 */
		return cameraURL;
	}
	public Camera(String id,String url){
		cameraID=id;
		cameraURL=url;
	}
}
