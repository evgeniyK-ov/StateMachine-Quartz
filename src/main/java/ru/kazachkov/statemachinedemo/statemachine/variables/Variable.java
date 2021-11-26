package ru.kazachkov.statemachinedemo.statemachine.variables;


public enum Variable {

    PROCESSING_STATUS(ProcessingStatus.class),
    PROCESSINGRESULT_STATUS(ProcessingResultStatus.class),
    MONITORING_STATUS(MonitoringStatus.class),
    ERROR_MESSAGE(String.class),
    PROCESS_NAME(String.class),
    SUBPROCESS_NAMES(String.class),
    SUBPROCESS_IDS(String.class),
    PARENT_PROCESS_ID(String.class),
    TYPE_PROCESS(ProcessTypes.class);

    private Class<?> cls;

    Variable(Class<?> sendingStatus) {
        cls = sendingStatus;
    }

}
