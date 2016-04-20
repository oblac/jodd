package jodd.proxetta.petite.data;

import jodd.petite.meta.PetiteBean;
import jodd.petite.meta.PetiteInject;

@PetiteBean ("___Bean1___")
public class Bean1 implements IBean1 {

    @PetiteInject ("++++Bean2++++")
    protected Bean2 aBean;

    @Override
    @Logged
    public Object doInBean_1() {
        return aBean.doInBean_2();
    }

}
