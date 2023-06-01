## 紧急TODO
- AI choosePlayer

## 约定
### 方法名：
- choose：玩家/AI进行具体选择，通常不带执行逻辑
- do：执行逻辑
- askFor：需要角色做出某种响应。通常是模板方法，内部可以调用choose（选择，交给子类实现）和do（执行）


## 设计细节
### 卡牌角色选择
- 三个API
  - ① 合法目标 = 卡牌定义的合法目标检测 + 合法性修改技能
  - ② 出牌阶段是否有可用目标 = ③中选第一个目标是否能选到
  - ③ 出牌阶段选下一个目标 = ① + 距离限制 + 是否自身 + 已有目标对新目标选择的影响
- 方法设计
  - A. originalValidTarget（可重写）: 卡牌定义的合法目标检测
  - B. validTarget: ① 合法目标 = A + 合法性修改技能
    - 这里触发合法性修改时机
  - C. validTargetDistance: B + 距离限制
    - 这里触发距离限制修改时机
  - D. canUseTo（可重写）: ③ 出牌阶段选下一个目标 = C + 是否自身 + 已有目标对新目标选择的影响
    - 这里可以通过不调用validTarget来绕过合法性修改技能和距离修改技能的判断（如【借刀杀人】）
    - 不用考虑已有目标过滤
  - E. getAvailableTargets: ② 出牌阶段是否有可用目标 = D，已选目标为空