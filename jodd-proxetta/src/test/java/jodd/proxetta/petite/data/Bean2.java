package jodd.proxetta.petite.data;

import jodd.petite.meta.PetiteBean;

@PetiteBean ("++++Bean2++++")
public class Bean2 implements IBean2 {

    @Override
    public Object doInBean_2() {
        return this;
    }

}
