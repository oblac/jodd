package jodd.petite.prox.example1.impl;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;
import jodd.petite.prox.Logged;
import jodd.petite.prox.example1.IMainPetiteBean;
import jodd.petite.prox.example1.ISubPetiteBean;
import jodd.petite.scope.SingletonScope;

@PetiteBean (scope = SingletonScope.class)
public class MainPetiteBean implements IMainPetiteBean {

    @PetiteInject
    ISubPetiteBean subPetiteBean;

    @Override
    @Logged
    public void execute() {
        subPetiteBean.execute_sub();
        System.out.println("executing " + this.getClass().getCanonicalName());
    }

}
