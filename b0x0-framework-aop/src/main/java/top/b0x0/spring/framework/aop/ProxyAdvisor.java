package top.b0x0.spring.framework.aop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.b0x0.spring.framework.aop.advice.Advice;
import top.b0x0.spring.framework.aop.advice.impl.AfterReturningAdvice;
import top.b0x0.spring.framework.aop.advice.impl.BeforeAdvice;
import top.b0x0.spring.framework.aop.advice.impl.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * 代理通知类
 *
 * @author zzzzbw
 * @since 2018/6/20 23:40
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProxyAdvisor {

    /**
     * 通知
     */
    private Advice advice;

    /**
     * AspectJ表达式切点匹配器
     */
    private ProxyPointcut pointcut;

    /**
     * 执行顺序
     */
    private int order;

    /**
     * 执行代理方法
     *
     * @param adviceChain 通知链
     * @return 目标方法执行结果
     * @throws Throwable Throwable
     */
    public Object doProxy(AdviceChain adviceChain) throws Throwable {
        Object result = null;
        Class<?> targetClass = adviceChain.getTargetClass();
        Method method = adviceChain.getMethod();
        Object[] args = adviceChain.getArgs();

        if (advice instanceof BeforeAdvice) {
            ((BeforeAdvice) advice).before(targetClass, method, args);
        }
        try {
            result = adviceChain.doAdviceChain();
            if (advice instanceof AfterReturningAdvice) {
                ((AfterReturningAdvice) advice).afterReturning(targetClass, result, method, args);
            }
        } catch (Exception e) {
            if (advice instanceof ThrowsAdvice) {
                ((ThrowsAdvice) advice).afterThrowing(targetClass, method, args, e);
            } else {
                throw new Throwable(e);
            }
        }
        return result;
    }
}
