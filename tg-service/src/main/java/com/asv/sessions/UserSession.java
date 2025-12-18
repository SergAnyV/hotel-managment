package com.asv.sessions;


import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import lombok.Getter;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Временная сессия пользователя, хранящийся в памяти приложения.
 * Содержит:
 * - тип активного процесса (ProcessType),
 * - текущий шаг (ProcessState),
 * - временные данные, введённые пользователем между шагами.
 * Жизнь сессии ограничена.
 */
@Getter
public class UserSession implements Serializable {

    private static final long serialVersionUID = 1L;


    private ProcessType processType = ProcessType.NONE;
    private ProcessState processState = ProcessState.NONE;
    private final Map<String, Object> temporaryData = new HashMap<>();
    private long lastActivity = System.currentTimeMillis();

    /**
     * Устанавливает тип процесса и сбрасывает текущий процесс (шаг и временные данные).
     *
     * @param processType тип процесса, который необходимо установить
     */
    public void setProcessType(ProcessType processType) {
        this.processType = processType;
        resetProcess();
    }

    /**
     * Устанавливает текущий шаг процесса и обновляет время последней активности.
     *
     * @param processState шаг процесса, который необходимо установить
     */
    public void setProcessState(ProcessState processState) {
        this.processState = processState;
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Сбрасывает текущий процесс: шаг и временные данные.
     */
    public void resetProcess() {
        this.processState = ProcessState.NONE;
        this.temporaryData.clear();
    }

    /**
     * Сбрасывает текущий тип процесса: шаг и временные данные.
     */
    public void resetProcessTypeAndState() {
        this.processType = ProcessType.NONE;
        this.processState = ProcessState.NONE;
        this.temporaryData.clear();
    }

    /**
     * Сохраняет временное значение под ключом.
     */
    public void putData(String key, Object value) {
        temporaryData.put(key, value);
    }

    /**
     * Получает значение по ключу с приведением типа.
     */
    public <T> T getData(String key, Class<T> type) {
        return (T) temporaryData.get(key);
    }

    /**
     * Инициирует новый процесс указанного типа: устанавливает тип, первый шаг,
     * очищает временные данные и обновляет время последней активности.
     *
     * @param processType тип процесса для запуска
     */
    public void startProcess(ProcessType processType) {
        this.processType = processType;
        this.processState = ProcessIdentification.getFirstStep(processType);
        this.temporaryData.clear();
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Переходит к следующему шагу в рамках текущего процесса, если такой существует.
     * Обновляет время последней активности при успешном переходе.
     *
     * @return {@code true}, если переход к следующему шагу выполнен успешно; {@code false}, если текущий шаг — последний
     */
    public boolean nextStep() {
        ProcessState nextState = ProcessIdentification.getNextStep(this.processType, this.processState);
        if (nextState != ProcessState.NONE) {
            this.processState = nextState;
            this.lastActivity = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    /**
     * Проверяет, является ли текущий шаг последним в рамках активного процесса.
     *
     * @return {@code true}, если текущий шаг — последний; {@code false} в противном случае
     */
    public boolean isLastStep() {
        return ProcessIdentification.isLastStep(this.processType, this.processState);
    }

    /**
     * Сохраняет данные под указанным ключом и автоматически переходит к следующему шагу,
     * если текущий шаг не является последним.
     *
     * @param key   ключ для сохранения значения
     * @param value значение для сохранения
     */
    public void saveDataAndContinue(String key, Object value) {
        putData(key, value);
        if (!isLastStep()) {
            nextStep();
        }
    }

    /**
     * Проверяет, есть ли у пользователя активный процесс (тип и шаг не равны NONE).
     *
     * @return {@code true}, если процесс активен; {@code false} в противном случае
     */
    public boolean hasActiveProcess() {
        return processType != ProcessType.NONE && processState != ProcessState.NONE;
    }

    /**
     * Возвращает строковое представление прогресса процесса в формате "Шаг X из Y".
     *
     * @return строка с прогрессом, если процесс активен и содержит шаги; пустая строка в противном случае
     */
    public String getProgress() {
        int currentStep = ProcessIdentification.getStepNumber(processType, processState);
        int totalSteps = ProcessIdentification.getQuantityOfSteps(processType);

        if (currentStep > 0 && totalSteps > 0) {
            return String.format("Шаг %d из %d", currentStep, totalSteps);
        }
        return "";
    }

    /**
     * Проверяет, является ли текущая комбинация типа процесса и шага допустимой.
     *
     * @return {@code true}, если состояние валидно; {@code false} в противном случае
     */
    public boolean isValidState() {
        return ProcessIdentification.isValidStep(processType, processState);
    }

}
