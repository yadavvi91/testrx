package org.yadavvi.testrx;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.yadavvi.testrx.RxJavaExample.WORDS;

/**
 * Created by vishal on 17/2/17.
 */
public class RxJavaExampleTest {

    @Test
    public void testInSameThread() {
        // given
        final List<String> result = new ArrayList<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribe(new Consumer<String>() {
            public void accept(@NonNull String s) throws Exception {
                result.add(s);
            }
        });

        // then
        assertThat(result, notNullValue());
        assertThat(result, hasSize(9));
        assertThat(result, hasItem(" 4. fox"));
    }

    @Test
    public void testUsingTestObserver() {
        // given
        TestObserver<String> observer = new TestObserver<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribe(observer);

        // then
        observer.assertComplete();
        observer.assertNoErrors();
        observer.assertValueCount(9);
        assertThat(observer.values(), hasItem(" 4. fox"));
    }

    @Test
    public void testFailure() {
        // given
        TestObserver<String> observer = new TestObserver<String>();
        Exception exception = new RuntimeException("aslkdfj");
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                })
                .concatWith(Observable.<String>error(exception));

        // when
        observable.subscribe(observer);

        // then
        observer.assertError(exception);
        observer.assertNotComplete();
    }

    @Test
    public void testUsingComputationScheduler() {
        // given
        TestObserver<String> observer = new TestObserver<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribeOn(Schedulers.computation())
                .subscribe(observer);

        // then
        observer.assertComplete();
        observer.assertNoErrors();
        assertThat(observer.values(), hasItem(" 4. fox"));
    }

    @Test
    public void testUsingBlockingCall() {
        // given
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        Iterable<String> results = observable
                .subscribeOn(Schedulers.computation())
                .blockingIterable();

        // then
        assertThat(results, notNullValue());
        /*assertThat(results, iterableWithSize(9));*/
        assertThat(results, hasItem(" 4. fox"));
    }

    @Test
    public void testUsingComputationSchedulerAwaitingTermination() {
        // given
        TestObserver<String> observer = new TestObserver<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribeOn(Schedulers.computation())
                .subscribe(observer);

        observer.awaitTerminalEvent(2, TimeUnit.SECONDS);

        // then
        observer.assertComplete();
        observer.assertNoErrors();
        assertThat(observer.values(), hasItem(" 4. fox"));
    }

    @Test
    public void testUsingComputationScheduler_awaitility() {
        // given
        final TestObserver<String> observer = new TestObserver<String>();
        Observable<String> observable = Observable.fromIterable(WORDS)
                .zipWith(Observable.range(1, Integer.MAX_VALUE), new BiFunction<String, Integer, String>() {
                    public String apply(@NonNull String word, @NonNull Integer index) throws Exception {
                        return String.format("%2d. %s", index, word);
                    }
                });

        // when
        observable.subscribeOn(Schedulers.computation())
                .subscribe(observer);

        await().timeout(2, TimeUnit.SECONDS)
                .until(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        return observer.valueCount();
                    }
                }, equalTo(9));

        // then
        observer.assertComplete();
        observer.assertNoErrors();
        assertThat(observer.values(), hasItem(" 4. fox"));
    }


}