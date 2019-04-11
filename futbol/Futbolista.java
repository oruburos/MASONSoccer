package futbol;

import java.awt.*;
import sim.portrayal.*;
import sim.util.*;
import sim.engine.*;

public class Futbolista extends Entity implements Steppable {

    
    public double PROB_TIRO;
    public double PROB_PASE;
    public double PROB_REGATE;
    public double PROB_TIEMPISTA;
    static int dorsal = 0;
    private int miDorsal;
    private int tipoJugador;
    boolean miEquipoEnFaseOfensiva;
    
    boolean tengoBalon;

    private int equipoID;
    public MutableDouble2D tempVector = new MutableDouble2D();

    //0 portero
    //1 defensa
    //2 medio
    //3 mediapunta
    //4 delantero
    //5 extremo
    public Futbolista(final double x, final double y, Color c, int tipoJugador, int idEquipo) {
        super(x, y, 2, c);
        dorsal++;
        miDorsal = dorsal;
        setTipoJugador(tipoJugador);
        setEquipoID(idEquipo);

        switch (tipoJugador) {

            case 0:
                PROB_TIRO = .8;
                PROB_PASE = .8;
                PROB_REGATE = .0;
                PROB_TIEMPISTA = .0;
                break;

            case 1:
                PROB_TIRO = .0;
                PROB_PASE = .99;
                PROB_REGATE = .4;
                break;
            case 2:
                PROB_TIRO = .0;
                PROB_PASE = .990;
                PROB_REGATE = .10;
                PROB_TIEMPISTA = .25;
                break;

            case 3:
                PROB_TIRO = .3;
                PROB_PASE = .25;
                PROB_REGATE = .25;
                PROB_TIEMPISTA = .20;
                break;
            case 4:
                PROB_TIRO = .7;
                PROB_PASE = .1;
                PROB_REGATE = .15;
                PROB_TIEMPISTA = .05;
                break;
            case 5:
                PROB_TIRO = .3;
                PROB_PASE = .3;
                PROB_REGATE = .3;
                PROB_TIEMPISTA = .1;
                break;
        }
    }

    @Override
    public void draw(Object object, final Graphics2D g, final DrawInfo2D info) {

        super.draw(object, g, info);

        final double width = info.draw.width * radius * 2;
        final double height = info.draw.height * radius * 2;

        g.setColor(Color.white);
        double d = velocity.angle();
        g.drawLine((int) info.draw.x,
                (int) info.draw.y,
                (int) (info.draw.x) + (int) (width / 2 * Math.cos(d)),
                (int) (info.draw.y) + (int) (height / 2 * Math.sin(d)));
    }

    public MutableDouble2D getForces(final FutbolEGC cancha) {
        sumVector.setTo(0, 0);
        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 500);
        //aqui percibo mi entorno
        double dist = 0;
      /*  double mass1;
        double mass2;
    */    int objetos = objs.numObjs;

        
        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa != this && !(cosa instanceof LineaBandas)
                    && !(cosa instanceof LineaMedioCampo) && !(cosa instanceof Porteria)
                    && !(cosa instanceof PorteriaRival) && !(cosa instanceof LineasAreas)) {

                dist = ((Entity) cosa).loc.distance(loc);

                if ((((Entity) cosa).radius + radius) * 1.5 > dist) {

                    //  System.out.println("lejos");
                    if (cosa instanceof Balon) {
                        miEquipoEnFaseOfensiva = true;
                      
                        Double2D lugarPorteria = this.posicionPorteriaContraria(cancha);

                        if (this.posicionPorteriaContraria(cancha).distance(this.loc.x, loc.y) > 45) {
                          //  System.out.println("lejos de porteria");

                            if (!rivalEnfrente(cancha)) {
                               // System.out.println("no tengo rival enfrente");
                                tengoBalon=true;
                                this.conducirBalon((Balon) cosa, new MutableDouble2D(lugarPorteria));
                            } else {
                                
                               // MutableDouble2D lugarCompa = this.localizaCompañero(cancha);
                           
                                    System.out.println("pasa balon");
                                    tengoBalon=false;
                                    this.pasaBalon((Balon) cosa, cancha );
                                }/* else {
                                    tengoBalon=true;
                                 //   System.out.println("voy solo");
                                    this.conducirBalon((Balon) cosa, new MutableDouble2D(lugarPorteria));
                                }*/
                            
                        } else {
                            // System.out.println("a tirar");
                            tengoBalon= false;
                            tirarPorteriaRival(cancha, (Balon) cosa);

                        }

                        //this.recolocar(cancha);
                        //}
                    }                                      
                }//acercarse al balon
                else if (cosa instanceof Balon) {

                    if (this.getTipoJugador() != 0) {
                        if (irPorElBalon(cancha)) {
                            this.cap = .15;
                            tempVector.subtract(((Entity) cosa).loc, loc);
                            tempVector.setLength(0.5);
                            sumVector.addIn(tempVector);
                        } else {

                            this.recolocar(cancha);
//-REGRESAR A PPOSICION
                            this.cap = .05;
                            
                            if ( posicionPartida.distance(loc) >2){//para acabar con la oscilacion
                                tempVector.subtract(this.posicionPartida, loc);
                                sumVector.addIn(tempVector);
                            }
                        }
                    } else {
                        if (balonEnElAreaPropia(cancha)) {
                            this.cap = .05;
                            tempVector.subtract(((Entity) cosa).loc, loc);
                            tempVector.setLength(0.5);
                            sumVector.addIn(tempVector);
                        } else {

                            //esperar ahi mismo
                            this.cap = 0.002;

                        }
                    }

                }
            }
        }

        sumVector.addIn(bump);
        bump.x = 0;
        bump.y = 0;
        return sumVector;
    }

    public void msg(String x) {
        //System.out.println(x);
    }

    
    public MutableDouble2D posicionCompaneroMejorUbicado(FutbolEGC entorno ){
        Bag objs = entorno.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 200);
        int objetos = objs.numObjs;

        
        float calificacion  = 0 ;
        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if ( cosa instanceof Futbolista){
                Futbolista f = ( Futbolista)cosa;
                
                if ( f.getEquipoID()== this.equipoID && f != this){
                
                   
                    if ( getEquipoID() ==1){
                    
                        if ( getY() > f.getY()){                            
                            return f.loc;                        
                        }
                    }
                    
                    if ( getEquipoID() ==2){
                    
                        if ( getY() < f.getY()){                            
                            return f.loc;                        
                        }
                    }
                    
                    
                    
                }
            
            }
            
        }
        
        
        System.out.println("no hay adelante");
         for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if ( cosa instanceof Futbolista){
                Futbolista f = ( Futbolista)cosa;
                
                if ( f.getEquipoID()== this.equipoID && f != this){
                   return f.loc;                                        
                }
            
            }
            
        }
        
        
        
        return null;
        
    }
    public void pasaBalon(Balon balon , FutbolEGC entorno ) {
        
        
        System.out.println("pasando balon");
        
        MutableDouble2D posicionCompanero = this.posicionCompaneroMejorUbicado(entorno );
        
        
        

        System.out.println(" pasando el balon teamiD" + this.getEquipoID());
        tempVector.subtract(posicionCompanero, balon.loc);
        //tempVector.normalize().multiplyIn(.95);
        tempVector.normalize().multiplyIn(.95);
        balon.velocity.addIn(tempVector);

    }

    public void conducirBalon(Balon balon, MutableDouble2D posicionCompanero) {

        this.cap = .05;
        // System.out.println(" conduzco el balon teamiD" + this.getEquipoID());
        tempVector.subtract(posicionCompanero, balon.loc);
        //tempVector.normalize().multiplyIn(.95);
        tempVector.normalize().multiplyIn(.005);
        
        balon.velocity.addIn(tempVector);

    }

    public void tirarPorteriaRival(FutbolEGC entorno, Balon balon) {

        System.out.println(" tirando porteria rival el balon teamiD" + this.getEquipoID() );
        MutableDouble2D lugarPorteria = null;
        Bag objs = entorno.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 200);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            
            if ( this.getEquipoID()==1){
                if (cosa instanceof PorteriaRival) {

                    PorteriaRival porteriaRival = (PorteriaRival) cosa;
                    Double2D locus = entorno.fieldEnvironment.getObjectLocation(porteriaRival);
                    lugarPorteria = new MutableDouble2D(locus.x, locus.y);
                    tempVector.subtract(lugarPorteria, balon.loc);
                    tempVector.normalize().multiplyIn(1.0);
                    balon.velocity.addIn(tempVector);
                }
            }
            else  if ( this.getEquipoID()==2){
                if (cosa instanceof Porteria) {

                Porteria porteriaRival = (Porteria) cosa;
                Double2D locus = entorno.fieldEnvironment.getObjectLocation(porteriaRival);
                lugarPorteria = new MutableDouble2D(locus.x, locus.y);
                 tempVector.subtract(lugarPorteria, balon.loc);
                tempVector.normalize().multiplyIn(1.0);
                balon.velocity.addIn(tempVector);
            }
            }
        }

    }

    public boolean irPorElBalon(FutbolEGC cancha) {

        //   System.out.println(" voy por el balon teamiD" + this.getEquipoID());
        //checo la posicion del balon y la posicion de un companero, si es mas cercana la del compa lo dejo ir y yo me paro, si es mas cercana la mia yo voy
        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 35);

        Balon balon = null;
        Futbolista masProximo = null;

        for (int x = 0; x < objs.numObjs; x++) {
            Object cosa = objs.objs[x];

            if (cosa != this && !(cosa instanceof LineaBandas) && !(cosa instanceof LineaMedioCampo)) {
                if (cosa instanceof Balon) {
                    balon = (Balon) cosa;
                }
                if ( cosa instanceof Futbolista && ((Futbolista) cosa).getEquipoID() == this.equipoID) {
                 masProximo = (Futbolista) cosa;

                   /* if (balon != null  && this.loc.distance(balon.loc) > masProximo.loc.distance(balon.loc) ){
                        return false;                 
                 }*/
                }

            }

        }
        
        if (balon != null && masProximo != null) {
            return this.loc.distance(balon.loc) < masProximo.loc.distance(balon.loc);
        } else {
            return false;
        }
    }

    public boolean balonEnElAreaPropia(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(this.loc), 20);

        Balon balon;

        for (int x = 0; x < objs.numObjs; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Balon) {

                balon = (Balon) cosa;
                double posXBalon = balon.getX();
                double posYBalon = balon.getY();

                if ( this.getEquipoID() ==1){
                return posXBalon > 90 && posXBalon < 160 && posYBalon > 5 && posYBalon < 25;
                }else{
                    return posXBalon > 90 && posXBalon < 160 && posYBalon > 115 && posYBalon < 135;
                }
            }
        }

        return false;
    }

    public boolean rivalEnfrente(final FutbolEGC cancha) {

        Double2D posicionRival = posicionRivalEnfrente(cancha);

        if (posicionRival == null) {
            return false;
        } else {
            return true;
        }

    }

    public MutableDouble2D localizaCompañero(final FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(loc.x, loc.y), 250);

        for (int x = 0; x < objs.numObjs; x++) {
            Object cosa = objs.objs[x];
            if (cosa != this && !(cosa instanceof LineaBandas) && !(cosa instanceof LineaMedioCampo) && !(cosa instanceof LineasAreas) && !(cosa instanceof Balon)) {

                if (cosa instanceof Futbolista && ((Futbolista) cosa).getEquipoID() == this.getEquipoID()) {
                    Futbolista compa = ((Futbolista) cosa);
                    // return new MutableDouble2D(cancha.fieldEnvironment.getObjectLocation(cosa));
                    System.out.println("compañero encontrado " + this.getEquipoID() + " pos : " + compa.loc);
                    return compa.loc;
                    
                }

            }
        }
        return null;
    }

    public void step(final SimState state) {
        FutbolEGC cancha = (FutbolEGC) state;

        // get force
        final MutableDouble2D force = getForces(cancha);

        // acceleration = f/m
        accel.multiply(force, 1 / mass); // resets accel

        velocity.addIn(accel);
        capVelocity();

        newLoc.add(loc, velocity);  // resets newLoc
        if (this.isValidMove(cancha, newLoc)) {
            
            if ( loc.distance(newLoc)>.0001)
            loc = newLoc;
        }
        cancha.fieldEnvironment.setObjectLocation(this, new Double2D(loc));
    }

    private void recolocar(FutbolEGC cancha) {
        //juego de posicion basado en x cosas

        MutableDouble2D ubicacionIdeal = new MutableDouble2D(posicionPartida);

       // msg("  " + this.getMiDorsal() + " equipo ID " + this.getEquipoID());
       // msg(" la pelota la tiene mi equipo?" + miEquipoEnFaseOfensiva);

        // Double2D posPortero = posicionPorteroCompanero(cancha);
        // msg(" en que posicion esta mi portero?" + posPortero);
        boolean rivalCerca = rivalCercano(cancha);
        msg(" hay rivales cercanos?" + rivalCerca);
        msg(" estoy adelante de la linea del balon?" + adelanteLineaBalon(cancha));
        //   msg(" Mi posicion de aranque es ?" + this.posicionPartida);
        msg(" Mi posicion de actual es ?" + this.loc.toCoordinates());
        msg(" ********************* ");

        this.cap = .05;

        if (rivalCerca && !miEquipoEnFaseOfensiva) { //mi equipo esta siendo atacado
            ubicacionIdeal.y = posicionPartida.y - 10;
            msg("defender");
        } else if (!rivalCerca && adelanteLineaBalon(cancha)) {

            ubicacionIdeal.y = posicionPartida.y + 15;
            msg(" adelanto posicion");
        } 


        tempVector.subtract(ubicacionIdeal, loc);
        sumVector.addIn(tempVector);

    }

    public Double2D posicionPorteriaContraria(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(125, 5), 600);
        int objetos = objs.numObjs;

        boolean buscarPorteria2 = this.getEquipoID() == 1;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (buscarPorteria2) {
                if (cosa instanceof PorteriaRival) {

                    return cancha.fieldEnvironment.getObjectLocation(cosa);

                }
            } else {
                if (cosa instanceof Porteria) {

                    return cancha.fieldEnvironment.getObjectLocation(cosa);

                }
            }
            //nunca llegara a este caso, puesto que siempre hay un portero en cada equipo y por ende, siempre se encontraa su pocicion.
        }
        return null;
    }

    public Double2D posicionPorteriaPropia(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(125, 5), 400);
        int objetos = objs.numObjs;

        boolean buscarPorteria2 = this.getEquipoID() == 2;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (buscarPorteria2) {
                if (cosa instanceof PorteriaRival) {

                    return cancha.fieldEnvironment.getObjectLocation(cosa);

                }
            } else {
                if (cosa instanceof Porteria) {

                    return cancha.fieldEnvironment.getObjectLocation(cosa);

                }
            }
            //nunca llegara a este caso, puesto que siempre hay un portero en cada equipo y por ende, siempre se encontraa su pocicion.
        }
        return null;
    }

    public Double2D posicionPorteroCompanero(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(125, 5), 40);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Futbolista) {

                Futbolista compa = (Futbolista) cosa;

                if (compa.getTipoJugador() == 0)//es portero?
                {
                    return cancha.fieldEnvironment.getObjectLocation(compa);
                }

            }
        }

        return null;//nunca llegara a este caso, puesto que siempre hay un portero en cada equipo y por ende, siempre se encontraa su pocicion.
    }

    public Double2D posicionRivalEnfrente(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(this.loc), 100);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa != this && cosa instanceof Futbolista
                    && ((Futbolista) cosa).getEquipoID() != this.getEquipoID()) {
                if (this.equipoID == 1) {
                    // System.out.println("se encontro futbolista contrario");
                }
                Futbolista otro = (Futbolista) cosa;
              // Double2D posicionPorteriaContraria= posicionPorteriaContraria(cancha);

                if (this.getEquipoID() == 1) {//la porteria queda al sur
                    if (this.getY() > otro.getY() && Math.abs(this.getX() - otro.getX()) < 20) {
                        System.out.println(" 1 se encontro rival " + otro.getEquipoID());
                        return new Double2D(otro.loc);
                    }
                } else {
                    if (this.getY() < otro.getY() && Math.abs(this.getX() - otro.getX()) < 20) {
                        System.out.println(" 2 se encontro rival " + otro.getEquipoID());
                        return new Double2D(otro.loc);
                    }

                }
            }
        }
        return null;
    }

    public boolean rivalCercano(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(this.loc), 20);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Futbolista && ((Futbolista) cosa).getEquipoID() != this.getEquipoID()) {

                //msg("rival detectado en posicion "+ cancha.fieldEnvironment.getObjectLocation(cosa).toCoordinates());
                //return cancha.fieldEnvironment.getObjectLocation(compa);
                return true;

            }
        }
        return false;
    }

    public boolean adelanteLineaBalon(FutbolEGC cancha) {

        double posBalonY = posicionBalon(cancha).y;
        double posicionMia = this.getY();

        if ( this.getEquipoID() ==1){
        return posBalonY < posicionMia;
        }else{
            return posBalonY > posicionMia;
        }

    }

    public Double2D posicionBalon(FutbolEGC cancha) {

        Bag objs = cancha.fieldEnvironment.getObjectsWithinDistance(new Double2D(this.loc), 200);
        int objetos = objs.numObjs;

        for (int x = 0; x < objetos; x++) {
            Object cosa = objs.objs[x];

            if (cosa instanceof Balon) {

                Balon compa = (Balon) cosa;
                return cancha.fieldEnvironment.getObjectLocation(compa);
            }
        }
        return null;//nunca llegara a este caso, puesto que siempre hay un balon
    }

    /**
     * @return the miDorsal
     */
    public int getMiDorsal() {
        return miDorsal;
    }

    /**
     * @param miDorsal the miDorsal to set
     */
    public void setMiDorsal(int miDorsal) {
        this.miDorsal = miDorsal;
    }

    /**
     * @return the tipoJugador
     */
    public int getTipoJugador() {
        return tipoJugador;
    }

    /**
     * @param tipoJugador the tipoJugador to set
     */
    private void setTipoJugador(int tipoJugador) {
        this.tipoJugador = tipoJugador;
    }

    /**
     * @return the equipoID
     */
    public int getEquipoID() {
        return equipoID;
    }

    /**
     * @param equipoID the equipoID to set
     */
    private void setEquipoID(int equipoID) {
        this.equipoID = equipoID;
    }

}
