/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package animatsV2;

import ec.util.MersenneTwisterFast;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.continuous.Continuous2D;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author Admin
 */
public class Animat implements Steppable, Stoppable, sim.portrayal.Oriented2D {


    float inyeccionAmbiente =(float) .01;


    static MersenneTwisterFast random;
    static int ID;
    private int ownId;
    public Double2D lastLoc = new Double2D(0, 0);
    public Double2D loc = new Double2D(0, 0);

    /* estado interno del agente*/
    private String metaActual;
    private boolean alive;
    private float hambre;
    private float sed;
   // private float vida;
    private float miedo;
    private float pereza;
    private float curiosidad;
    private float vision;
    private float agresividad;
    private int muerte;
    private boolean dormido;
    private int dormidoDesde;//numero de paso desde el cual se durmio.
    private float limitehambre;
    private float limitesed;
    private float limitepereza;
    private float limitecuriosidad;


    public double orientation2D() {
        return Math.atan2(loc.y - lastLoc.y, loc.x - lastLoc.x);
    }

    /**
     * @return the metaActual
     */
    public String getMetaActual() {
        return metaActual;
    }

    /**
     * @param metaActual the metaActual to set
     */
    public void setMetaActual(String metaActual) {
        this.metaActual = metaActual;
    }

    public boolean isAlive() {

        if (this.alive) {
            if (getHambre() > getLimitehambre() + this.muerte) {
                setAlive(false);
                this.stop();
                return false;
            }
            if (getSed() > getLimitesed() + this.muerte) {
                setAlive(false);
                this.stop();
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    public Animat() {
        super();
        ID++;
        this.setAlive(true);
        this.setDormido(false);
        this.ownId = ID;
        random = new MersenneTwisterFast();
        this.setLimitesed((float) (random.nextDouble() * 100));
        this.setSed(getLimitesed() / 2);

        this.setLimitehambre((float) (random.nextDouble() * 100));
        this.setHambre(this.getLimitehambre() / 2);

        this.setVision(20);

       // this.setVida((float) 100);
        this.muerte = random.nextInt(1000);


        this.setMiedo((float) (random.nextInt(100)));
        this.setAgresividad((float) (random.nextInt(100)));

        //this.setPereza((float) (random.nextDouble() * 50));
        this.setPereza(100f);
        this.setLimitepereza(100f);

    }

    //public void draw
    private boolean despertarAnimat(SimulacionAnimats entorno) {

        return entorno.numPasos - 1500 > this.dormidoDesde;
    }

    @Override
    public void step(SimState state) {
        final SimulacionAnimats entorno = (SimulacionAnimats) state;
        if (this.isAlive()) {
            if (entorno.numPasos < 10000) {
                entorno.numPasos++;
            } else {
                entorno.numPasos = 0;
            }
            loc = entorno.ambiente.getObjectLocation(this);

            lastLoc = loc;
            if (this != null) {
                synchronized(this){
                checarPercepciones(entorno);
                int comportamiento = this.checarMotivaciones(entorno);
                //int desicionComportamiento= libreAlbedrio(comportamiento);

                loc = elegirComportamiento(comportamiento, entorno);

                loc = checaLimites();
                if (loc.equals(lastLoc)) {
                    loc = curiosear(entorno);
                }
                }
            }

            //    msg("numero de paso " + entorno.numPasos + "\n" + this.toString() + "\n" + loc.toCoordinates());
            entorno.ambiente.setObjectLocation(this, loc);
        }


    }

    public Double2D checaLimites() {
//La simulacione sta discretizada a un universo de 100 x 100

        if (loc.x > 100) {
            if (loc.y > 100) //return new Double2D(99.99,99.99);
            {
                return new Double2D(0, 0);
            } else {
                return new Double2D(0, loc.y);
            }
        }
        if (loc.x < 0) {
            if (loc.y < 0) {
                return new Double2D(99.99, 99.99);
            } //return new Double2D(0,0);
            else {
                return new Double2D(99.99, loc.y);
            }
        }

        if (loc.y < 0) {
            if (loc.x < 0) {
                return new Double2D(99.99, 99.99);
            } else {
                return new Double2D(loc.x, 99.99);
            }
        }

        if (loc.y > 100) {
            if (loc.x < 0) {
                return new Double2D(99.99, 0);
            } else {
                return new Double2D(loc.x, 99.99);
            }
        }
        return loc;

    }

    /**
     * @param limitecuriosidad the limitecuriosidad to set
     */
    public void setLimitecuriosidad(float limitecuriosidad) {
        this.limitecuriosidad = limitecuriosidad;
    }

    public Double2D metaSatisfacerHambre(SimulacionAnimats entorno) {


        Bag cosas = entorno.ambiente.allObjects;
        Iterator it = cosas.iterator();
        //checo lo que existe en el ambiente
        while (it.hasNext()) {
            Object cosa = it.next();
            //comiendo animat carroña

            if (cosa instanceof Animat) {

                //estoy interesado en comida
                Animat animat = (Animat) cosa;
                if (!animat.isAlive()) {
                    Double2D lugarComida = entorno.ambiente.getObjectLocation(animat);
                    //si alcanzo a divisar alguna comida
                    if (this.loc.distance(lugarComida) < this.vision) {
                        //msg("Buscando carroña");
                        if (this.loc.distance(lugarComida) < entorno.limiteProximidad) {
                            entorno.ambiente.remove(animat);
                        }
                        return this.loc;
                    } else {
                        //msg(" Hay Carroñeo en " + lugarComida.toCoordinates());
                        Double2D puntoMedio = calculaSiguientePunto(this.loc, lugarComida);
                        // //System.out.println(" Punto medio" + puntoMedio.toCoordinates());
                        return puntoMedio;
                    }
                }
            }
            if (cosa instanceof Comida) {
                //estoy interesado en comida
                Comida comida = (Comida) cosa;
                Double2D lugarComida = entorno.ambiente.getObjectLocation(comida);
                //si alcanzo a divisar alguna comida
                if (this.loc.distance(lugarComida) < this.vision) {
                    if (this.loc.distance(lugarComida) < entorno.limiteProximidad) {

                        int nutrientes = comida.getNutrientes();
                        float espacioPanza = this.getHambre() - nutrientes;
                        //msg(" Como " + Math.abs(espacioPanza) + " nutrientes");
                        if (espacioPanza < 0) {

                            comida.setNutrientes((int) (Math.abs(espacioPanza)));
                            this.setHambre(0);
                        } else {
                            // msg(" Como " + nutrientes + " nutrientes");
                            this.setHambre(this.getHambre() - nutrientes);
                            entorno.ambiente.remove(comida);
                        }
                        return this.loc;
                    } else {
                        //System.out.println(" Hay comida en " + lugarComida.toCoordinates());
                        Double2D puntoMedio = calculaSiguientePunto(this.loc, lugarComida);
                        //System.out.println(" Punto medio" + puntoMedio.toCoordinates());
                        return puntoMedio;
                    }
                }
            }


        }



        //no habia comida a la vista

        ////System.out.println(" No hay comida a la vista");
        return this.loc;

    }

    public Double2D metaSatisfacerSed(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();
        //checo lo que existe en el ambiente


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Agua) {
                //estoy interesado en Agua
                Agua agua = (Agua) cosa;
                Double2D lugarAgua = entorno.ambiente.getObjectLocation(agua);
                //si alcanzo a divisar alguna Agua


                if (this.loc.distance(lugarAgua) < this.vision) {

                    if (this.loc.distance(lugarAgua) < entorno.limiteProximidad) {



                        int nutrientes = agua.getNutrientes();


                        float espacioPanza = this.getSed() - nutrientes;
                        //msg(                                " Como " + Math.abs(espacioPanza) + " nutrientes");


                        if (espacioPanza < 0) {

                            agua.setNutrientes((int) (Math.abs(espacioPanza)));


                            this.setSed(0);


                        } else {
                            //msg(" Como " + nutrientes + " nutrientes");


                            this.setHambre(this.getHambre() - nutrientes);
                            entorno.ambiente.remove(agua);



                        }

                        //System.out.println(" Tomo agua ");
                      //  entorno.ambiente.remove(agua);


                        return this.loc;


                    } else {
                        //  //System.out.println(" Veo Agua ,  a por ella  ");
                        Double2D puntoMedio = calculaSiguientePunto(this.loc, lugarAgua);


                        return puntoMedio;



                    }
                }
            }
        }
        //no habia Agua a la vista
        ////System.out.println(" No hay Agua a la vista");


        return this.loc;



    }

    public Double2D metaSatisfacerPereza(SimulacionAnimats entorno) {


        //msg("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");

        if (despertarAnimat(entorno)) {
            //msg("Despertando animat " + this.ownId);
            this.setPereza(0);
            this.setDormido(false);
        } else {
            //msg("Durmiendo animat " + this.ownId);
        }
        return this.loc;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Double2D elegirComportamiento(int opcion, SimulacionAnimats entorno) {


    //msg("comportamiento adecuado " + opcion);

        switch (opcion) {
            case 1: {
                synchronized(this){
              //  this.setMetaActual("Evadir Obstaculo");
                return this.evadirObstaculo(entorno);
                }
            }
            case 2: {
                synchronized(this){
                this.setMetaActual("Satisfacer Sed");
                return this.metaSatisfacerSed(entorno);
                }
            }
            case 3: {
                synchronized(this){
                this.setMetaActual("Satisfacer Hambre");
                return this.metaSatisfacerHambre(entorno);
                }
            }
            case 4: {
                synchronized(this){
                this.setMetaActual("Descansar");
                return this.metaSatisfacerPereza(entorno);
                }
            }

            case 5: {
                synchronized(this){

                return this.encuentroSemejante(entorno);
                }
            }
            case 6: {
                synchronized(this){
                this.setMetaActual("Huir de Depredador");
                return this.huirDepredador(entorno);
                }
            }
            default:
                return this.loc;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(" Animat ID: ").append(this.ownId).append("\n");

        if (this.isAlive()) {
            sb.append(" Vivo").append("\n");
        } else {
            sb.append("Muerto").append("\n");
        }
        sb.append(" Nivel Animat /  Nivel activacion").append("\n");
        sb.append(" Meta Actual " + this.getMetaActual()).append("\n");
        sb.append(" Hambre " + this.getHambre()).append(" / ").append(this.getLimitehambre()).append("\n");
        sb.append(" Sed " + this.getSed()).append(" / ").append(this.getLimitesed()).append("\n");
        sb.append(" Pereza " + this.getSed()).append(" / ").append(this.getLimitesed()).append("\n");
        sb.append(" Miedo/Agresividad " + this.getMiedo() + " / " + this.getAgresividad()).append("\n");
        sb.append(" Nivel Muerte " + this.muerte).append("\n");

        return sb.toString();
    }

    /**
     * @return the ownId
     */
    public int getOwnId() {
        return ownId;




    }

    /**
     * @param ownId the ownId to set
     */
    public void setOwnId(int ownId) {
        this.ownId = ownId;



    }

    public boolean precondicionesSatisfacerSed() {
        return this.getSed() > this.getLimitesed();


    }

    public boolean precondicionesSatisfacerHambre() {
        return this.getHambre() > this.getLimitehambre();


    }

    private void msg(String aux) {
        //System.out.println(" *********************");
        //System.out.println(" *********************");
        System.out.println(aux);
        //  //System.out.println(" *********************");
        //System.out.println(" *********************");
    }

    private Double2D calculaSiguientePunto(Double2D origen, Double2D destino) {


        double trasladoX = (destino.x - origen.x) / 350;


        double trasladoY = (destino.y - origen.y) / 350;
        //  msg( " ubicacion actual " +loc.toCoordinates());
        // msg( " ubicacion comdida " + lugarComida.toCoordinates());
        //msg( "traslados calculados x"+ trasladoX +" traslado y "+ trasladoY);
        Double2D puntoMedio = new Double2D(this.loc.x + trasladoX, this.loc.y + trasladoY);
        //msg( " COORDENADA CALCULADA "+ puntoMedio.toCoordinates());


        return puntoMedio;



    }

    private boolean precondicionesSatisfacerPereza(SimulacionAnimats entorno) {
        if (!this.isDormido()) {
            this.setDormidoDesde(entorno.numPasos);
            return (this.getPereza() > this.limitepereza);
        } else {
            return true;
        }

    }

    private boolean precondicionesEvadirObstaculo(SimulacionAnimats entorno) {


        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();
        //checo lo que existe en el ambiente


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Obstaculo) {
                Obstaculo obstaculo = (Obstaculo) cosa;
                Double2D lugarObstaculo = entorno.ambiente.getObjectLocation(obstaculo);


                if (this.lastLoc.distance(lugarObstaculo) < entorno.limiteProximidad) {
                    return true;
                }
                
                double longitudObstaculo = this.loc.distance(lugarObstaculo);
                if (longitudObstaculo+obstaculo.getLongitud() < entorno.limiteProximidad) {
                    return true;
                }
            }

        }
        return false;


    }

    private Double2D evadirObstaculo(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Obstaculo) {
                //estoy interesado en Obstaculo
                Obstaculo Obstaculo = (Obstaculo) cosa;
                Double2D lugarObstaculo = entorno.ambiente.getObjectLocation(Obstaculo);
                //si alcanzo a divisar alguna comida


                //if (this.loc.distance(lugarObstaculo) < entorno.limiteProximidad) {
                    if (this.loc.distance(lugarObstaculo) < this.vision) {

                    this.setMetaActual("obstaculo cerca");
///return evadirObstaculo(loc, lugarObstaculo, entorno);
                    return evadirObstaculo(loc, lugarObstaculo,entorno);


                }

                  if (this.loc.distance(lugarObstaculo) +4< entorno.limiteProximidad) {

///return evadirObstaculo(loc, lugarObstaculo, entorno);
                    return evadirObstaculo(loc, lugarObstaculo,entorno);


                }
                if (this.loc.distance(lugarObstaculo) -4< entorno.limiteProximidad) {

                    
                    ///return evadirObstaculo(loc, lugarObstaculo, entorno);
                    return evadirObstaculo(loc, lugarObstaculo,entorno);

                }
                    return this.loc;


                
            }
        }
        return this.loc;


    }

    private boolean checarSemejante(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Animat) {
                //estoy interesado en Animat
                Animat Animat = (Animat) cosa;


                if (Animat.ownId != this.ownId && Animat.isAlive()) {
                    Double2D lugarSemejante = entorno.ambiente.getObjectLocation(Animat);
                    //si alcanzo a divisar algun semejante y no soy yo


                    if (this.loc.distance(lugarSemejante) < this.vision) {
                        return true;


                    }
                }
            }
        }
        return false;


    }

    public Double2D encuentroSemejante(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Animat) {
                //estoy interesado en Animat
                Animat Animat = (Animat) cosa;


                if (Animat.ownId != this.ownId && Animat.isAlive()) {
                    Double2D lugarSemejante = entorno.ambiente.getObjectLocation(Animat);
                    //si alcanzo a divisar algun semejante y no soy yo


                    if (this.loc.distance(lugarSemejante) < this.vision) {

                        //de acuerdo a mi codificacion actuo, si con miedo o con agresividad
                        if (this.getAgresividad() > this.getMiedo()) {
                            if (this.loc.distance(lugarSemejante) < entorno.limiteProximidad) {
                                //mordisco de daño aleatorio
                                int mordisco = random.nextInt(5);


                                this.setHambre(this.getHambre() - mordisco);
                                if (this.getHambre() < 0) {
                                    this.setHambre(0);
                                    this.setAgresividad(0);
                                }
                                this.setAgresividad(this.getAgresividad() - mordisco);
                                if (this.getAgresividad() < 0) {
                                    this.setAgresividad(0);
                                    this.setMiedo(100);
                                }
                                //msg(" ANIMAT " + this.ownId + " come " + mordisco + " a animat " + Animat.ownId);
                                Animat.setHambre(Animat.getHambre() + mordisco);
                                Animat.setAgresividad(Animat.agresividad + mordisco);


                            }
                            this.setMetaActual("Acercar a semejante");
                            return this.calculaSiguientePunto(this.loc, lugarSemejante);

                        } else {
                            this.setMetaActual("huir de semejante");
                            return this.huir(loc, lugarSemejante);

                        }
                    }
                }
            }
        }
        return this.loc;


    }

    private Double2D huir(Double2D origen, Double2D destino) {

        double trasladoX = (destino.x - origen.x) / 25;
        double trasladoY = (destino.y - origen.y) / 25;
        Double2D puntoFuga = new Double2D(this.loc.x - trasladoX, this.loc.y - trasladoY);

        return puntoFuga;
    }

    public Double2D curiosear(SimulacionAnimats entorno) {

        int paso = entorno.numPasos % 12000;
        int opcion = this.ownId % 5;//numAnimats;

        switch (opcion) {
            case 0: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x + 0.03, this.loc.y + 0.03);
                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y + 0.05);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {
                    return new Double2D(this.loc.x - 0.02, this.loc.y - 0.05);

                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {
                    return new Double2D(this.loc.x + 0.03, this.loc.y - 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y + 0.05);


            }
            case 1: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y + 0.05);


                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {

                    return new Double2D(this.loc.x + 0.05, this.loc.y + 0.05);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {
                    return new Double2D(this.loc.x + 0.03, this.loc.y - 0.05);



                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y - 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y + 0.05);


            }
            case 2: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y + 0.05);


                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y - 0.05);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {

                    return new Double2D(this.loc.x + 0.03, this.loc.y - 0.05);


                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {

                    return new Double2D(this.loc.x + 0.05, this.loc.y + 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y + 0.05);


            }
            case 3: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y - 0.05);


                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {
                    return new Double2D(this.loc.x + 0.03, this.loc.y - 0.05);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {
                    return new Double2D(this.loc.x + 0.05, this.loc.y + 0.05);


                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y + 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y + 0.05);


            }
            case 4: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x + 0.00, this.loc.y - 0.01);


                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {
                    return new Double2D(this.loc.x + 0.03, this.loc.y - 0.01);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {
                    return new Double2D(this.loc.x + 0.02, this.loc.y + 0.02);


                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {
                    return new Double2D(this.loc.x + 0.02, this.loc.y + 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y - 0.05);


            }

            default: {
                if (paso > 0 && paso < 1000 || paso > 3500 && paso < 5000 || paso > 10500) {
                    return new Double2D(this.loc.x - 0.00, this.loc.y - 0.01);


                }

                if (paso > 1000 && paso < 2500 || paso > 9000 && paso < 10500) {
                    return new Double2D(this.loc.x - 0.03, this.loc.y + 0.04);


                }

                if (paso > 2500 && paso < 3500 || paso > 5000 && paso < 6200) {
                    return new Double2D(this.loc.x - 0.02, this.loc.y - 0.02);


                }
                if (paso > 3500 && paso < 5000 || paso > 6200 && paso < 9000) {
                    return new Double2D(this.loc.x + 0.02, this.loc.y - 0.05);


                }
                return new Double2D(this.loc.x - 0.05, this.loc.y - 0.05);


            }

        }
    }

    /**
     * @return the hambre
     */
    public float getHambre() {
        return hambre;


    }

    /**
     * @param hambre the hambre to set
     */
    public void setHambre(float hambre) {
        this.hambre = hambre;


    }

    /**
     * @return the sed
     */
    public float getSed() {
        return sed;


    }

    /**
     * @param sed the sed to set
     */
    public void setSed(float sed) {
        this.sed = sed;


    }

    /**
     * @return the miedo
     */
    public float getMiedo() {
        return miedo;


    }

    /**
     * @param miedo the miedo to set
     */
    public void setMiedo(float miedo) {
        this.miedo = miedo;


    }

    /**
     * @return the pereza
     */
    public float getPereza() {
        return pereza;


    }

    /**
     * @param pereza the pereza to set
     */
    public void setPereza(float pereza) {
        this.pereza = pereza;


    }

    /**
     * @return the curiosidad
     */
    public float getCuriosidad() {
        return curiosidad;


    }

    /**
     * @param curiosidad the curiosidad to set
     */
    public void setCuriosidad(float curiosidad) {
        this.curiosidad = curiosidad;


    }

    /**
     * @return the vision
     */
    public float getVision() {
        return vision;


    }

    /**
     * @param vision the vision to set
     */
    public void setVision(int vision) {
        this.vision = vision;


    }

    /**
     * @return the limitehambre
     */
    public float getLimitehambre() {
        return limitehambre;


    }

    /**
     * @param limitehambre the limitehambre to set
     */
    public void setLimitehambre(float limitehambre) {
        this.limitehambre = limitehambre;


    }

    /**
     * @return the limitesed
     */
    public float getLimitesed() {
        return limitesed;


    }

    /**
     * @param limitesed the limitesed to set
     */
    public void setLimitesed(float limitesed) {
        this.limitesed = limitesed;


    }

    /**
     * @return the limitepereza
     */
    public float getLimitepereza() {
        return limitepereza;


    }

    /**
     * @param limitepereza the limitepereza to set
     */
    public void setLimitepereza(float limitepereza) {
        this.limitepereza = limitepereza;


    }

    /**
     * @return the limitecuriosidad
     */
    public float getLimitecuriosidad() {
        return limitecuriosidad;


    }

    /**
     * @return the agresividad
     */
    public float getAgresividad() {
        return agresividad;


    }

    /**
     * @param agresividad the agresividad to set
     */
    public void setAgresividad(float agresividad) {
        this.agresividad = agresividad;


    }


    /**
     * @return the dormido
     */
    public boolean isDormido() {
        return dormido;
    }

    /**
     * @param dormido the dormido to set
     */
    public void setDormido(boolean dormido) {
        this.dormido = dormido;
    }

    /**
     * @return the dormidoDesde
     */
    public int getDormidoDesde() {
        return dormidoDesde;
    }

    /**
     * @param dormidoDesde the dormidoDesde to set
     */
    public void setDormidoDesde(int dormidoDesde) {
        this.dormidoDesde = dormidoDesde;
    }

    private int checarMotivaciones(SimulacionAnimats entorno) {



        // if(!checarDepredador(entorno)){

        if (checarDepredador(entorno)) {
            return 6;
        }

        this.setHambre((float) (this.getHambre() + entorno.desgastoXPaso));
        this.setSed((float) (this.getSed() + entorno.desgastoXPaso));
        this.setPereza((float) (this.getPereza() + entorno.desgastoXPaso));

        if (precondicionesEvadirObstaculo(entorno)) {
            return 1;
        }
        if (precondicionesSatisfacerSed()) {
            return 2;// this.metaSatisfacerSed(entorno);
        }
        if (precondicionesSatisfacerHambre()) {
            return 3;//this.metaSatisfacerHambre(entorno);
        }
        if (precondicionesSatisfacerPereza(entorno)) {
            return 4;//this.metaSatisfacerPereza(entorno);
        }

        if (checarSemejante(entorno)) {
            return 5;//this.encuentroSemejante(entorno);
        }
        return 6;
        /*}else{
        if (precondicionesEvadirObstaculo(entorno)) {
        return 1;
        }
        else{
        return 6;
        }



        }*/

    }

    public void stop() {
        this.setAlive(false);
        //msg("#LLAMANDO STOP # en aminat " + this.ownId);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean checarDepredador(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Depredador) {
                //estoy interesado en Animat
                Depredador Depredador = (Depredador) cosa;


                Double2D lugarDepredador = entorno.ambiente.getObjectLocation(Depredador);
                //si alcanzo a divisar algun sdepredador

                if (this.loc.distance(lugarDepredador) < this.vision) {
                    return true;


                }
            }
        }
        return false;
    }

    public Double2D huirDepredador(SimulacionAnimats entorno) {

        Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Depredador) {
                //estoy interesado en Animat
                Depredador Depredador = (Depredador) cosa;


                Double2D lugarDepredador = entorno.ambiente.getObjectLocation(Depredador);
                //si alcanzo a divisar algun semejante y no soy yo


                if (this.loc.distance(lugarDepredador) < this.vision) {

                    return this.huir(loc, lugarDepredador);

                }
            }
        }

        return this.loc;


    }



    private Double2D evadirObstaculo(Double2D loc, Double2D lugarObstaculo, SimulacionAnimats entorno) {




        Point2D lugar = new Point2D() {

            double x;
            double y;
            @Override
            public double getX() {
                return x;
            }

            @Override
            public double getY() {
                return y;
            }

            @Override
            public void setLocation(double x, double y) {
                x= x;
                y= y;
            }
        };

        lugar.setLocation(loc.x, loc.y);

        Rectangle2D.Double rect = new Rectangle2D.Double(lugarObstaculo.x, lugarObstaculo.y, 10, 4);

        if (rect.contains(lugar)) {

            return this.lastLoc;

        }
        double trasladoX = (lugarObstaculo.x - loc.x) / 120;
        double trasladoY = (lugarObstaculo.y - loc.y) / 120;
        Double2D puntoFuga = new Double2D(this.loc.x - trasladoX, this.loc.y + trasladoY);

        return this.huir(loc, lugarObstaculo);

        //return loc;


   
    }

    private void checarPercepciones(SimulacionAnimats entorno) {
        //aqui iran las funciones de Patti Maes


         Bag cosas = entorno.ambiente.allObjects;


        Iterator it = cosas.iterator();


        while (it.hasNext()) {
            Object cosa = it.next();


            if (cosa instanceof Depredador) {
                Depredador Depredador = (Depredador) cosa;
                Double2D lugarDepredador = entorno.ambiente.getObjectLocation(Depredador);
                if (this.loc.distance(lugarDepredador) < this.vision) {
                    
                    this.setMiedo( this.getMiedo()+ this.inyeccionAmbiente );

                }
            }
            if( cosa instanceof Comida){
            if ( this.loc.distance(entorno.ambiente.getObjectLocation(cosa)) <this.vision){

                this.setHambre(this.getHambre()+this.inyeccionAmbiente);
            }

            }
            if( cosa instanceof Agua){
            if ( this.loc.distance(entorno.ambiente.getObjectLocation(cosa)) <this.vision){

                this.setSed(this.getSed()+this.inyeccionAmbiente);
            }

            }

        }
        
    }

    private int libreAlbedrio(int comportamiento) {

       float moneda = random.nextFloat();
        float probabilidad = random.nextFloat();

        if( moneda > probabilidad ){
            return comportamiento;
        }else{
            return  random.nextInt(6);
        }
        

    }
}
