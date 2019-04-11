
package futbol;

import java.awt.*;
import java.util.ArrayList;
import sim.engine.*;
import static sim.engine.SimState.doLoop;
import sim.field.continuous.*;
import sim.util.*;

public class FutbolEGC extends SimState {

    /** @todo handle realocation of grids when these two are changed */
    GameManagerEGC dt = new GameManagerEGC();

    public double xMin = 0;
    public double xMax = 240;
    public double yMin = 0;
    public double yMax = 140;
    public double nivelTiro = 120;
public Balon ba;
    
    public int golEquipo1= 0;
    public int golEquipo2= 0;
    
    public Continuous2D fieldEnvironment;

/*
    public ArrayList equipo = new ArrayList();
    public ArrayList rival = new ArrayList();

*/
    /** Creates a Futbol simulation with the given random number seed. */
    public FutbolEGC(long seed) {
        this(seed, 240, 140);
    }

    public FutbolEGC(long seed, int width, int height) {
        super(seed);
        xMax = width;
        yMax = height;
        createGrids();
    }

    
    public void registraGol( boolean golDeEquipo1){
        
        if (golDeEquipo1){
            golEquipo1++;
        }else{
            golEquipo2++;
        }
        
        if( golEquipo1>2 || golEquipo2>2){
            kill();
        }else{
            saqueCentral();
        }
    
    }
    
    void saqueCentral(){
        dt.initGame();    
        System.out.println("****Marcador actual " + golEquipo1 + " vs " + golEquipo2);
    }
    void creaEquipos(){

    for ( Futbolista actual:dt.equipo){

      fieldEnvironment.setObjectLocation( actual , new Double2D(actual.getX(), actual.getY()));
      schedule.scheduleRepeating(actual);

    }
    for ( Futbolista actual:dt.equipoRival){

      fieldEnvironment.setObjectLocation( actual , new Double2D(actual.getX(), actual.getY()));
      schedule.scheduleRepeating(actual);

    }
    
    }


    void createGrids() {
         fieldEnvironment = new Continuous2D(5, (xMax - xMin), (yMax - yMin));
    }

   public Double2D  posicionPorteroCompanero() {

        Bag objs =this.fieldEnvironment.getObjectsWithinDistance(new Double2D(125,5 ), 200);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Futbolista ) {

               Futbolista compa  = (Futbolista) cosa;

               if ( compa.getTipoJugador()==0)//es portero?
                return fieldEnvironment.getObjectLocation(compa);
                
            }
        }

            return null;//nunca llegara a este caso, puesto que siempre hay un portero en cada equipo y por ende, siempre se encontraa su pocicion.
    }

 public void start() {
        super.start();  // clear out the schedule
        createGrids();

       // Futbolista b;
        //Rival rival;
        double x, y;

        creaEquipos();


        x = 125;
        y = 70;

        ba = new Balon(x, y);
        
        
        creaCancha();
        fieldEnvironment.setObjectLocation(ba, new Double2D(x, y));
        schedule.scheduleRepeating(ba);

    }
 
 public void centraBalon(){
     ba.setX(125);
     ba.setY(70);
 
 }
 public void creaCancha(){
 
/*
 *Campo completo
 *
 */
        for (int linea = 5; linea < 135; linea++) {
            fieldEnvironment.setObjectLocation(new LineaBandas(), new Double2D(50, linea));
        }
        for (int linea = 5; linea < 135; linea++) {
            fieldEnvironment.setObjectLocation(new LineaBandas(), new Double2D(200, linea));
        }
        for (int linea = 50; linea < 200; linea++) {
            fieldEnvironment.setObjectLocation(new LineaBandas(), new Double2D(linea, 5));
        }
        for (int linea = 50; linea < 200; linea++) {
            fieldEnvironment.setObjectLocation(new LineaBandas(), new Double2D(linea, 135));
        }
        /* Linea medio campo*/
        for (int linea = 50; linea < 200; linea++) {
            fieldEnvironment.setObjectLocation(new LineaMedioCampo(), new Double2D(linea, 70));
        }

        /**Areas */
        for (int linea =  92; linea < 158; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(linea, 25));
        }
         for (int linea =  92; linea < 158; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(linea, 115));
        }
         for (int linea =  5 ; linea < 25 ; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(92,linea));
        }
          for (int linea =  115 ; linea < 135 ; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(92,linea));
        }
            for (int linea =  5 ; linea < 25 ; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(158,linea));
        }
          for (int linea =  115 ; linea < 135 ; linea++) {
            fieldEnvironment.setObjectLocation(new LineasAreas(), new Double2D(158,linea));
        }


            fieldEnvironment.setObjectLocation(new Porteria(), new Double2D(125,2));
            fieldEnvironment.setObjectLocation(new PorteriaRival(), new Double2D(125,135));



 
 }
    public static void main(String[] args) {
        doLoop(FutbolEGC.class, args);
        System.exit(0);
    }
}
    
    
    
    
    
