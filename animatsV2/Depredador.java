package animatsV2;

import java.util.Iterator;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author Omar Verduga
 */
public class Depredador implements Steppable {


    /*Miembros de la clase*/
    static int idDepreador;
    private String meta;
    private int ownID;
    private Double2D ubicacion;
    private double vision;

    //constante default para valor de vision
    private final float visionDefault = 30f;


    /**
     * Constructor default de la clase
     */
    public Depredador() {

        this.setOwnID(idDepreador++);
        this.setVision( visionDefault );
    }


    /**
     * Constructor que toma la ubicacion
     * @param  ubicacion es donde se colocara la nueva instancia  dedepredador.
     */
    public Depredador(Double2D ubicacion) {

        this.ubicacion = ubicacion;
        this.setVision( visionDefault );
        this.setOwnID(idDepreador++);

    }

    //Metodos
    /** Metodo para obtener el siguiente punto hacia donde se movera el depredador
     * @param  origen es la ubicacion actual
     * @param destino es la ubicacion a la que se quiere llegar
     */
    private Double2D calculaSiguientePunto(Double2D origen, Double2D destino) {

        double trasladoX = (destino.x - origen.x) / 200;
        double trasladoY = (destino.y - origen.y) /200;

        Double2D puntoMedio = new Double2D(this.ubicacion.x + trasladoX, this.ubicacion.y + trasladoY);

        return puntoMedio;

    }

    /**Metodo step para implementar la interfaz Steppable*
     * @param state estado actual de la simulacion
     */
    public void step(SimState state) {

        SimulacionAnimats entorno = (SimulacionAnimats) state;

        Bag cosas = entorno.ambiente.allObjects;
        Iterator it = cosas.iterator();
        //checo lo que existe en el ambiente
        while (it.hasNext()) {
            Object cosa = it.next();
            if (cosa instanceof Animat) {

                //estoy interesado en comida             
                Animat animat = (Animat) cosa;
                Double2D lugarComida = entorno.ambiente.getObjectLocation(animat);
                //si alcanzo a divisar alguna comida
                if (this.ubicacion.distance(lugarComida) < this.getVision() && animat.isAlive()) {
                  // Si me encuentro lo suficientemente cerca
                    if (this.ubicacion.distance(lugarComida) < entorno.limiteProximidad) {
                        this.setMeta("Cazar");
                        animat.setAlive(false);                  
                        animat.stop();
                        entorno.ambiente.remove(animat);//Comi al animat y lo borro del ambiente
                    }
                    Double2D puntoMedio = calculaSiguientePunto(this.ubicacion, lugarComida);
                    
                    ubicacion = puntoMedio;              
                    entorno.ambiente.setObjectLocation(this, ubicacion);
                    return; //actualizo mi posicion
                }
                this.setMeta("Esperando");//El depredador esta en estado esperar
            }
        }
    }

  /**
     * @return  meta actual
     */
    public String getMeta() {
        return meta;
    }

    /**
     * @param meta actualiza la meta a perseguir
     */
    public void setMeta(String meta) {
        this.meta = meta;
    }
    /**
     * @return the ownID
     */
    public int getOwnID() {
        return ownID;
    }

    /**
     * @param ownID the ownID to set
     */
    public void setOwnID(int ownID) {
        this.ownID = ownID;
    }

    /**
     * @return ubicacion actual
     */
    public Double2D getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion la nueva ubicacion de la instancia
     */
    public void setUbicacion(Double2D ubicacion) {
        this.ubicacion = ubicacion;
    }

    /**
     * @return el rango de vision actual
     */
    public double getVision() {
        return vision;
    }

    /**
     * @param vision el nuevo rango de valor
     */
    public void setVision(double vision) {
        this.vision = vision;
    } 
}