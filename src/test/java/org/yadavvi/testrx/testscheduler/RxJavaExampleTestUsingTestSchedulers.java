package org.yadavvi.testrx.testscheduler;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.yadavvi.testrx.RxJavaExample.WORDS;

/**
 * Created by vishal on 17/2/17.
 */
public class RxJavaExampleTestUsingTestSchedulers {

    @Test
    public void testUsingTestSchedulers() {
        // given:
        TestScheduler scheduler = new TestScheduler();
        TestObserver<String> observer = new TestObserver<String>();
        Observable<Long> tick = Observable.interval(1, TimeUnit.SECONDS, scheduler);

        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(tick, new BiFunction<String, Long, String>() {
                    public String apply(@NonNull String word, @NonNull Long index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        observable.subscribeOn(scheduler)
                .subscribe(observer);

        // expect:
        observer.assertNoValues();
        observer.assertNotComplete();

        // when:
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // then:
        observer.assertNoErrors();
        observer.assertValueCount(1);
        observer.assertValues(" 0. the");

        // when:
        scheduler.advanceTimeTo(9, TimeUnit.SECONDS);
        // then
        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(9);
        assertThat(observer.values(), hasItem(" 3. fox"));

    }

}