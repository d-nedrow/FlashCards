/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flashcards.drivers;

import flashcards.model.ExampleClass;

/**
 *
 * @author Daniel
 */
public class ExampleClassDriver {
    public static void testExampleClass() {
        int x = 7;
        ExampleClass example = new ExampleClass(x);
        System.out.println("The data field of this example is " + 
                example.getExampleField());
    }
}
