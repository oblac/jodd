package jodd.petite.prox.example1;

import jodd.petite.meta.PetiteInject;

public class ExternalBean {

    @PetiteInject
    private IMainPetiteBean mainPetiteBean;

    public void execute() {
        mainPetiteBean.execute();
        System.out.println("executing non jodd petite bean -> " + this.getClass().getCanonicalName());
    }

}
