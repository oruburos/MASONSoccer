package futbol;
import sim.util.*;
import sim.portrayal.simple.*;
import java.awt.*;

public abstract  class Entity extends OvalPortrayal2D
    {
    public MutableDouble2D loc, velocity, bump;

    public MutableDouble2D posicionPartida;
    public MutableDouble2D force = new MutableDouble2D();
    public MutableDouble2D accel = new MutableDouble2D();
    public MutableDouble2D newLoc = new MutableDouble2D();
    public MutableDouble2D sumVector = new MutableDouble2D(0,0);

    public double speed;
    public double radius;
    private boolean adentro;    
    public double cap;
    public double mass;

    
   
    
    public double getMass() { return mass; }
    public void setMass( double newMass ) { mass = newMass; } 
    
    // Constructor
    public Entity( double newX, double newY, double newRadius, Color c )
        {
        super(c, newRadius * 2); 
        
        loc = new MutableDouble2D(newX, newY);
        posicionPartida =new MutableDouble2D(loc);
        velocity = new MutableDouble2D(0, 0);
        bump = new MutableDouble2D(0, 0);
        radius = newRadius;


        
        mass = 4.0;//antes 2
        cap = 1.0;
        
        speed = 8;//antes 1.8
        }
    
 public boolean isValidMove( final FutbolEGC keepaway, final MutableDouble2D newLoc)
        {
        Bag objs = keepaway.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 10);

        double dist = 0;

        // check objects
        for(int x=0; x<objs.numObjs; x++)
            {
            if(objs.objs[x] != this&& !(objs.objs[x] instanceof LineaBandas) && !(objs.objs[x] instanceof Porteria) && !(objs.objs[x] instanceof LineaMedioCampo) && !(objs.objs[x] instanceof LineasAreas) && !(objs.objs[x] instanceof PorteriaRival) )
                {
                dist = ((Entity)objs.objs[x]).loc.distance(newLoc);

                if((((Entity)objs.objs[x]).radius + radius) > dist)  // collision!
                    return false;
                }
            }


           // this.setAdentro(true);
        // check walls
        //if(newLoc.x > keepaway.xMax)
        if(newLoc.x > 200)
            {
            if (velocity.x > 0) velocity.x = -velocity.x;
            return false;
            }
        //else if(newLoc.x < keepaway.xMin)
            else if(newLoc.x < 50)
            {
            if (velocity.x < 0) velocity.x = -velocity.x;
            return false;
            }
       // else if(newLoc.y > keepaway.yMax)
         else if(newLoc.y > 135)
            {
            if (velocity.y > 0) velocity.y = -velocity.y;
            return false;
            }
        //else if(newLoc.y < keepaway.yMin)
        else if(newLoc.y < 5)
            {
            if (velocity.y < 0) velocity.y = -velocity.y;
            return false;
            }



        // no collisions: return, fool
        return true;
        }
    public void capVelocity()
        {
        if(velocity.length() > cap)
            velocity = velocity.setLength(cap);
        }

    /**
     * @return the adentro
     */
    public boolean isAdentro() {
        return adentro;
    }

    /**
     * @param adentro the adentro to set
     */
    public void setAdentro(boolean adentro) {
        this.adentro = adentro;
    }



 public double getX() { return loc.x; }
    public void setX( double newX ) { 
            if ( Math.abs(loc.x -  newX )>3){
    
        loc.x = newX;
            }}
    
    public double getY() { return loc.y; }
    public void setY( double newY ) {
        if ( Math.abs(loc.y -  newY )>3){
        
        loc.y = newY; 
        }
    }
    
    public double getVelocityX() { return velocity.x; }
    public void setVelocityX( double newX ) { 
        
        
        velocity.x = newX; }
    
    public double getVelocityY() { return velocity.y; }
    public void setVelocityY( double newY ) { velocity.y = newY; }
 
    public double getSpeed() { return speed; }
    public void setSpeed( double newSpeed ) { speed = newSpeed; }   
    
    public double getRadius() { return radius; }
    public void setRadius( double newRadius ) 
        {
        radius = newRadius;
        scale = 4 * radius;  // so our ovalportrayal knows how to draw/hit us right
        } 

    }

