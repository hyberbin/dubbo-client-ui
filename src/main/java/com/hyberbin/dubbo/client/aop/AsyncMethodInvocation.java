package com.hyberbin.dubbo.client.aop;

import com.hyberbin.dubbo.client.config.Async;
import com.hyberbin.dubbo.client.config.CoderQueenModule;
import com.hyberbin.dubbo.client.ui.frames.DubboUIFrame;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 防止界面卡顿，方法异步化
 */
@Slf4j
public class AsyncMethodInvocation implements MethodInterceptor {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 10,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Async async = methodInvocation.getMethod().getAnnotation(Async.class);
        executor.submit(() -> {
            DubboUIFrame frame = CoderQueenModule.getInstance(DubboUIFrame.class);
            try {
                frame.setLogFrameActive();
                frame.getAppTree().setEnabled(false);
                frame.showBusyLabel(async.value());
                methodInvocation.proceed();
            } catch (Throwable throwable) {
                log.error("执行异步任务出错", throwable);
            } finally {
                frame.getAppTree().setEnabled(true);
                frame.finishBusyLabel(async.value());
            }
        });
        return null;
    }


}
