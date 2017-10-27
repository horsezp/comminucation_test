package com.leo.hystrix.demo;

import org.junit.Assert;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public class RequestCacheCommand extends HystrixCommand<String> {  
    private final int id;  
    public RequestCacheCommand( int id) {  
        super(HystrixCommandGroupKey.Factory.asKey("RequestCacheCommand"));  
        this.id = id;  
    }  
    @Override  
    protected String run() throws Exception {  
        System.out.println(Thread.currentThread().getName() + " execute id=" + id);  
        return "executed=" + id;  
    }  
    //��дgetCacheKey����,ʵ�����ֲ�ͬ������߼�  
    @Override  
    protected String getCacheKey() {  
        return String.valueOf(id);  
    }  
   
    public static void main(String[] args){  
        HystrixRequestContext context = HystrixRequestContext.initializeContext();  
        try {  
            RequestCacheCommand command2a = new RequestCacheCommand(2);  
            RequestCacheCommand command2b = new RequestCacheCommand(2);  
            Assert.assertTrue(command2a.execute()!=null);  
            //isResponseFromCache�ж��Ƿ����ڻ����л�ȡ���  
            Assert.assertFalse(command2a.isResponseFromCache());  
            Assert.assertTrue(command2b.execute()!=null);  
            Assert.assertTrue(command2b.isResponseFromCache());  
        } finally {  
            context.shutdown();  
        }  
        context = HystrixRequestContext.initializeContext();  
        try {  
            RequestCacheCommand command3b = new RequestCacheCommand(2);  
            Assert.assertTrue(command3b.execute()!=null);  
            Assert.assertFalse(command3b.isResponseFromCache());  
        } finally {  
            context.shutdown();  
        }  
    }  
}  