/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package animatsV2;

/**
 *
 * @author Admin
 */
public class Comida {

    /* atributos fisicos*/
    private int nutrientes;
  


public Comida(){
    this.setNutrientes(100);
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
