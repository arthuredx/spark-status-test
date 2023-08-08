# procedimentos

Problemas de dependencia:
```shell
mvn dependency:tree -Dverbose=true > treemvn.txt
```

``` shell
aws s3 cp s3://temp.datahub.hotmart.com/tests/spark-status-1.0-jar-with-dependencies.jar spark-status-1.0-jar-with-dependencies.jar  --dryrun
```

## Test script spark SUCCEEDED - (State: FINISHED, Final Status: SUCCEEDED)

1. Script spark iniciado corretamente
2. Não mantém o código do spark em sleep
3. Aguarda terminar sozinho

Resultado: YARN consegue pegar status de sucesso do spark e finaliza com sucesso.

```shell
spark-submit --master yarn --deploy-mode cluster --name FinishedSucceededSleep0 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test KilledKilled --sleep 0
```

## Test script spark iniciado com “erro” - (State: Failed, Final Status: Failed) 

Script spark iniciado com “erro” (usado master local[*] diferente do informado no spark-submit) e encerrando sozinho master local[*]

1. App spark iniciado errado (master local[*]) mantendo o app do yarn em state accepted
2. Não mantém o código do spark em sleep
3. Aguarda terminar sozinho

Resultado: Spark executou, no histórico do spark considerou como sucesso, mas YARN considerou como falha do spark e do app do YARN porque o código que executou considerou um master local[*] e não o master yarn informado no spark-submit:

```shell
spark-submit --master yarn --deploy-mode cluster --name FailedFailedSleep0 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test FailedFailed --sleep 0
```

## Test kill via YARN UI - (State: KILLED, Final Status: KILLED)

1. App spark iniciado corretamente
2. Executado select
3. Deixado em sleep
4. Feito kill via UI do YARN

Resultado: Spark executou o primeiro job, mas como foi feito kill antes de finalizar o spark o YARN marcou com killed tanto para o app do YARN quanto para o Spark

```shell
spark-submit --master yarn --deploy-mode cluster --name KilledKilledSleep120 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test KilledKilled --sleep 120
```


## Test Falha devido ultrapassar spark.driver.maxResultSize - (State: FINISHED, Final Status: FAILED)

1. App spark iniciado corretamente e setando spark.driver.maxResultSize abaixo do que será coletado
4. Executado collect()

Resultado: Spark falhou e não executou corretamente o job o que seria necessário uma nova execução.

```shell
spark-submit --master yarn --deploy-mode cluster --conf spark.driver.maxResultSize=512b --name CollectMiBKilledBySparkDriverSleep120 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test CollectMiB --target 2048 --sleep 120
```

## Test Falha Driver - User class threw exception: java.lang.OutOfMemoryError: Java heap space - (State: FINISHED, Final Status: FAILED)

Resultado: spark falhou mas não fez kill da aplicação. 
Os dados desse teste não foram documentados. A princípio é mesmo do anterior, mas deixar aqui como um segundo exemplo.

Mesmo com OnOutOfMemoryError ficou com status de failed para o spark.

```shell
spark-submit --master yarn --deploy-mode cluster --driver-java-options "-XX:OnOutOfMemoryError=kill" --conf "spark.executor.extraJavaOptions=-XX:OnOutOfMemoryError=kill" --conf spark.driver.memory=800m --conf spark.executor.memory=1g --name CollectMiBKilledBySparkDriverMemorySleep120 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test CollectMiB --target 100048 --sleep 120
```

```shell
spark-submit --master yarn --deploy-mode client --conf spark.ui.killEnabled=true --name CollectMiBKilledByClientModeSleep300 --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test CollectMiB --target 100048 --sleep 300
```

# Kill de executor via jps -v | grep application_id + kill +9 PID (State: FAILED, Final Status: FAILED)
Não documentado ainda, mas mesmo matando 1 executor gerou status failed
```shell
spark-submit --master yarn --deploy-mode client --conf spark.ui.killEnabled=true --name CollectMiBTest --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test CollectMiB --target 10048 --output /tmp/tst/ --sleep 300
```

# Kill de executor via stouro de memória
Não documentado ainda
```shell
# 
spark-submit --master yarn --deploy-mode cluster --driver-java-options "-XX:OnOutOfMemoryError=kill" --conf "spark.executor.extraJavaOptions=-XX:OnOutOfMemoryError=kill" --conf spark.driver.memory=3g --conf spark.driver.cores=2 --conf spark.executor.memory=450m --conf spark.executor.instances=2 --conf spark.executor.cores=2 --name CollectTest --class org.example.Main spark-status-1.0-jar-with-dependencies.jar --test CollectMiB --target 10000048 --sleep 120
```