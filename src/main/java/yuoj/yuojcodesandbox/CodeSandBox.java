package yuoj.yuojcodesandbox;

import yuoj.yuojcodesandbox.model.ExecuteCodeRequest;
import yuoj.yuojcodesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
