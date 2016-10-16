package jodd.petite.prox.example1.impl;

import jodd.petite.meta.PetiteBean;
import jodd.petite.prox.Logged;
import jodd.petite.prox.example1.ISubPetiteBean;
import jodd.petite.scope.ProtoScope;

@PetiteBean (scope = ProtoScope.class)
public class SubPetiteBean implements ISubPetiteBean {

    @Override
    @Logged
    public void execute_sub() {
        System.out.println("Executing " + this.getClass().getCanonicalName());
    }

}
