Please document here:
* Classes you have implemented or modified, including test classes
* Any assumptions that affected your design
* Any shortcomings of your implementation
* An explanation of your definition of fairness execution

- how is the queue filled? frequency ?
- do jobs keep coming into queue once run() is called?
    YES, external systems put jobs in queue
- When jobrunner.run() is called, jobs in queue are fixed and we can't add any new jobs?
    NO, jobs keep coming : simulate it in test
- how often is run() called?
    ONCE
- what happens if a job has VERY HIGH duration ? should we accept it? once consumed from queue, can't abort & run only ONCE. so...
- do jobs run in parallel? if yes, what is degree of parallelism?
    Runtime.getRuntime().availableProcessors() ? 12 ?

Questions
- can i use ExecuterService threadpool ?
- do jobs keep coming into JobQueue once run() is called & while it is running?
- should all tests in NaiveJobRunnerTests pass without modification?
- FIFO & Fairness to customers are opposite, right? If we try being FAIR, we can't do FIFO.
- once job starts, can't end, but what if duration is high? It will hog cpu, contradicting FAIRNESS.
