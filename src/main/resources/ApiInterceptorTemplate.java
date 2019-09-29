import com.hyberbin.dubbo.client.runner.Interceptor;
import java.util.Map;


public class ApiInterceptor implements Interceptor {

    /**
     * 将map参数预处理
     * @param args
     * @return 返回实际运行的参数
     */
    public void before(Map<String, Object> context,Map args) {
    }

    /**
     * 处理方法运行的结果返回自己想要的
     * @param o
     * @return
     */
    public Object after(Object o) {
        return o;
    }
}
