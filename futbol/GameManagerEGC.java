/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package futbol;

import java.awt.Color;
import java.util.ArrayList;
import sim.util.Double2D;



public class GameManagerEGC {

    ArrayList<Futbolista> equipo;
    ArrayList<Futbolista> equipoRival;
    //

    
    
    public void initGame(){
    
      equipo = new ArrayList<Futbolista>();
        
        Futbolista actual = new Futbolista(125, 10, Color.yellow, 0 , 1);
        
        equipo.add(actual);
       
    /*    actual = new Futbolista(125, 45, Color.red, 1, 1);
        equipo.add(actual);
*/
        actual = new Futbolista(75, 45, Color.red, 5, 1);
        equipo.add(actual);

  /*      actual = new Futbolista(175, 35, Color.red, 1, 1);
        equipo.add(actual);
*/

        actual = new Futbolista(125, 55, Color.red, 2, 1);
        equipo.add(actual);

/*
        actual = new Futbolista(155, 65, Color.red, 2, 1);
        equipo.add(actual);

        actual = new Futbolista(90, 65, Color.red, 2, 1);
        equipo.add(actual);

*/
        actual = new Futbolista(145, 70, Color.red,1, 1);
        equipo.add(actual);

  /*      actual = new Futbolista(105, 85, Color.red, 4, 1);
        equipo.add(actual);

        actual = new Futbolista(65, 100, Color.red, 1, 1);
        equipo.add(actual);
*/
        actual = new Futbolista(165, 100, Color.red, 5, 1);
        equipo.add(actual);

        equipoRival = new ArrayList<Futbolista>();


        Futbolista rivalActual = new Futbolista(125, 120, Color.ORANGE, 0, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(120, 95, Color.BLUE, 1, 2);
        equipoRival.add(rivalActual);

       rivalActual = new Futbolista(120, 65, Color.BLUE, 5, 2);
        equipoRival.add(rivalActual);
/*
        rivalActual = new Futbolista(120, 100, Color.BLUE, 5, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(90, 95, Color.BLUE, 1, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(150, 95, Color.BLUE, 2, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(150, 80, Color.BLUE, 2, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(90, 75, Color.BLUE, 3, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(150, 60, Color.BLUE, 3, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(70, 35, Color.BLUE, 4, 2);
        equipoRival.add(rivalActual);

        rivalActual = new Futbolista(120, 35, Color.BLUE, 1 , 2);
        equipoRival.add(rivalActual);
*/
    }
    public GameManagerEGC() {
    initGame();

    }
}
