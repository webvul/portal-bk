import static junit.framework.TestCase.assertEquals;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestSafeTL {

	ScheduledExecutorService executeService= Executors.newScheduledThreadPool(10);

	
	
	
	@Test
	public void testQueue() throws InterruptedException {
		
		TransferQueue<String> queue=new LinkedTransferQueue<String>();
		
		queue.tryTransfer("abc");
		queue.tryTransfer("bcd");
		
		assertEquals("abc",queue.take());
		
		
	}
	
	@Test
	public void test() throws IOException {



		for(int i=0;i<10;i++){


			final int idx=i;
			executeService.schedule(()->{
				try {

					set("data"+idx);
					Thread.sleep((idx%3)*10);
					compare();
					Thread.sleep((idx%2)*10);
					set("test"+idx);
					Thread.sleep((idx%5)*10);
					compare("test"+idx);
					Thread.sleep((idx%3)*10);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			},i%3, TimeUnit.SECONDS);

		}

		System.in.read();

	}


	public void set(String data){

		SafeTLDemo.setData(data);

	}

	public void compare(){
		String a=SafeTLDemo.getData();

		String b=SafeTLDemo.getData2();

		assertEquals(a, StringUtils.substring(b,0,b.length()-1));

	}

	public void compare(String data){
		String a=SafeTLDemo.getData();

		String b=SafeTLDemo.getData2();

		assertEquals(a, StringUtils.substring(b,0,b.length()-1));

		assertEquals(a, data);

		System.out.println(data);

	}

}
