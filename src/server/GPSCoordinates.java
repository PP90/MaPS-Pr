package server;

public class GPSCoordinates {
    private final double lat;
    private final double lon;
    private final int EARTH_RADIUS_KM=6371;
    private double rSmall;
    private double deltaLon;
     private double deltaLat;
    
    public GPSCoordinates(double lat, double lon){
    this.lat=getRadians(lat);
    this.lon=getRadians(lon);
    }
  
    public void setDistance(double distanceKm){
        setRsmall(distanceKm);
        setDeltaLon();
        setDeltaLat();
    }
    
    private double getRadians(double input){
    return Math.toRadians(input);
    }
    
    private void setRsmall(double distanceKm){
    this.rSmall=distanceKm/EARTH_RADIUS_KM;
    }
    
    private double getLatT(){
    return Math.asin(Math.sin(lat)/Math.cos(this.rSmall));
    }
    
    private double getLonT(){
    return Math.asin(Math.sin(lon)/Math.cos(this.rSmall));
    }
    
    
    private void setDeltaLon(){
        this.deltaLon=Math.asin(Math.sin(rSmall)/Math.cos(lat));
 
    }
    
    private void setDeltaLat(){
        this.deltaLat=Math.asin(Math.sin(rSmall)/Math.cos(lon));
    }
    
    
    public double getLonMin(){
    double minLon=lon-this.deltaLon;
      minLon=Math.toDegrees(minLon);
    return minLon;
    }
    
     public double getLatMin(){
    double minLat=lat-this.deltaLat;
      minLat=Math.toDegrees(minLat);
    return minLat;
    }
     
    public double getLonMax(){
    double maxLon=lon+this.deltaLon;
    maxLon=Math.toDegrees(maxLon);
    return maxLon;
    }
    
    public double getLatMax(){
    double maxLat=lat+this.deltaLat;
    maxLat=Math.toDegrees(maxLat);
    return maxLat;
    }
    
    public void toStringInfo(){
    System.out.println("MinLon is:"+ getLonMin());
        System.out.println("MaxLon is:"+ getLonMax());
        System.out.println("MinLat is:"+ getLatMin());
        System.out.println("MaxLat is:"+ getLatMax());
    }
}
