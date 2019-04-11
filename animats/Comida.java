/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package animats;

/**
 *
 * @author Admin
 */
public class Comida {

    /* atributos fisicos*/
    private int nutrientes;
  


public Comida(){
    this.setNutrientes(50);
}

    /**
     * @return the nutrientes
     */
    public int getNutrientes() {
        return nutrientes;
    }

    /**
     * @param nutrientes the nutrientes to set
     */
    public void setNutrientes(int nutrientes) {
        this.nutrientes = nutrientes;
    }

}
