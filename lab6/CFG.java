package cn.edu.fudan.analysis;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.dexbacked.instruction.*;
import org.jf.dexlib2.iface.*;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;

import java.util.*;


public class CFG {

    /**
     * The method that the control flow graph is built on
     */
    private Method targetMethod = null;

    /**
     * All the basic blocks EXCEPT catch blocks.
     */
    private HashSet<BasicBlock> blocks = new HashSet<BasicBlock>();

    /**
     * Entry block of this method
     * */
    private BasicBlock entryBB;

    private CFG(){}

    public static String classType2Name(String s) {
        if (s == null) return "";

        if (s.startsWith("L"))
            s = s.substring(1);

        String res = s.replace(";", "").replace("$", "~").replace("/",".");
        return res;
    }

    public static String methodSignature2Name(Method m) {
        String temp = m.getName() + "(";
        List<? extends CharSequence> parameters = m.getParameterTypes();
        List<String> params = new ArrayList<>();
        for (CharSequence p : parameters) {
            String param = p.toString();
            String suffix = "";
            if (param.startsWith("["))
            {
                suffix = "[]";
                param = param.substring(1);
            }
            switch (param)
            {
                case "B":
                    params.add("byte" + suffix);
                    break;
                case "C":
                    params.add("char" + suffix);
                    break;
                case "D":
                    params.add("double" + suffix);
                    break;
                case "F":
                    params.add("float" + suffix);
                    break;
                case "I":
                    params.add("int" + suffix);
                    break;
                case "J":
                    params.add("long" + suffix);
                    break;
                case "S":
                    params.add("short" + suffix);
                    break;
                case "V":
                    params.add("void" + suffix);
                    break;
                case "Z":
                    params.add("boolean" + suffix);
                    break;
                default:
                    String tmp = classType2Name(param);

                    if (tmp.contains("~"))
                        tmp = tmp.substring(tmp.lastIndexOf('~') + 1);

                    params.add(tmp + suffix);
                    break;
            }
        }

        temp += String.join(",", params);
        temp += ")";
        return temp;
    }

    public static CFG createCFG(Method method) {
        CFG cfg = new CFG();
        cfg.targetMethod = method;
        int entry = 0;
        int flp = 0;
        int switch_origin = 0;
        Iterable<? extends Instruction> instructions = cfg.targetMethod.getImplementation().getInstructions();
        Vector offsets = new Vector();
        Vector switch_des = new Vector();

        for (Instruction i : instructions) {
            DexBackedInstruction dbi = (DexBackedInstruction) i;
            if (flp == 0) {
                entry = dbi.instructionStart;
                flp = 1;
            }
            int offset = 0;
            switch (dbi.opcode) {
                case GOTO:
                    offset = ((DexBackedInstruction10t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset: " + offset);
                    offsets.add(offset);
                    break;
                case GOTO_16:
                    offset = ((DexBackedInstruction20t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset: " + offset);
                    offsets.add(offset);
                    break;
                case GOTO_32:
                    offset = ((DexBackedInstruction30t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset: " + offset);
                    offsets.add(offset);
                    break;
                case IF_EQ:
                case IF_NE:
                case IF_LT:
                case IF_GE:
                case IF_GT:
                case IF_LE:
                    offset = dbi.instructionStart + dbi.getOpcode().format.size;
                    System.out.println(dbi.getOpcode() + ", offset1: " + offset);
                    offsets.add(offset);
                    offset = ((DexBackedInstruction22t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset2: " + offset);
                    offsets.add(offset);
                    break;
                case IF_EQZ:
                case IF_NEZ:
                case IF_LTZ:
                case IF_GEZ:
                case IF_GTZ:
                case IF_LEZ:
                    offset = dbi.instructionStart + dbi.getOpcode().format.size;
                    System.out.println(dbi.getOpcode() + ", offset1: " + offset);
                    offsets.add(offset);
                    offset = ((DexBackedInstruction21t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset2: " + offset);
                    offsets.add(offset);
                    break;

                case PACKED_SWITCH:
                case SPARSE_SWITCH:
                    offset = ((DexBackedInstruction31t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", switch payload offset: " + offset);
                    offsets.add(offset);
                    switch_origin = dbi.instructionStart;
                    break;

                case PACKED_SWITCH_PAYLOAD:
                case SPARSE_SWITCH_PAYLOAD:
                    List<? extends SwitchElement> switchElements = null;
                    if (dbi instanceof DexBackedPackedSwitchPayload)
                        switchElements = ((DexBackedPackedSwitchPayload) dbi).getSwitchElements();
                    else
                        switchElements = ((DexBackedSparseSwitchPayload) dbi).getSwitchElements();

                    for (SwitchElement s : switchElements) {
                        System.out.println(dbi.getOpcode() + ", offset: " + s.getOffset());
                        offsets.add(s.getOffset()* 2 + switch_origin);
                        switch_des.add(s.getOffset()* 2 + switch_origin);
                    }
                    break;
                case MOVE_EXCEPTION:
                    offset = dbi.instructionStart;
                    System.out.println(dbi.getOpcode() + ", offset: " + offset);
                    offsets.add(offset);
            }
        }

        System.out.println(offsets);
        BasicBlock nowblock = new BasicBlock(method, entry);
        flp = 0;
        for (Instruction i : instructions) {
            DexBackedInstruction dbi = (DexBackedInstruction) i;
            if (dbi.getOpcode() == Opcode.PACKED_SWITCH || dbi.getOpcode() == Opcode.SPARSE_SWITCH) { //正常的代码switch之后不会没有指令了，所以不会出错
                nowblock.addInstruction(i);
                cfg.blocks.add(nowblock);
                nowblock = new BasicBlock(method, dbi.instructionStart + dbi.getOpcode().format.size);
            }
            else if (offsets.contains(dbi.instructionStart)) {
                if (flp == 0) {
                    cfg.entryBB = nowblock;
                    flp = 1;
                }
                if (nowblock.getInstructions() == null){
                    nowblock.addInstruction(i);
                }
                else {
                    cfg.blocks.add(nowblock);
                    nowblock = new BasicBlock(method, dbi.instructionStart);
                    nowblock.addInstruction(i);
                }
            }
            else
                nowblock.addInstruction(i);
        }
        cfg.blocks.add(nowblock);
        for (BasicBlock i : cfg.blocks) {
            Instruction m = i.getInstructions().get(i.getInstructions().size() - 1);
            DexBackedInstruction dbi = (DexBackedInstruction) m;
            int offset = 0;

            offset = dbi.instructionStart + dbi.getOpcode().format.size;
            switch (dbi.opcode) {
                case GOTO:
                    offset = ((DexBackedInstruction10t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    linkBlock(cfg,i,offset);
                    break;
                case GOTO_16:
                    offset = ((DexBackedInstruction20t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    linkBlock(cfg,i,offset);
                    break;
                case GOTO_32:
                    offset = ((DexBackedInstruction30t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    linkBlock(cfg,i,offset);
                    break;
                case IF_EQ:
                case IF_NE:
                case IF_LT:
                case IF_GE:
                case IF_GT:
                case IF_LE:
                    linkBlock(cfg,i,offset);
                    offset = ((DexBackedInstruction22t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    linkBlock(cfg,i,offset);
                    break;
                case IF_EQZ:
                case IF_NEZ:
                case IF_LTZ:
                case IF_GEZ:
                case IF_GTZ:
                case IF_LEZ:
                    linkBlock(cfg,i,offset);
                    offset = ((DexBackedInstruction21t) dbi).getCodeOffset() * 2 + dbi.instructionStart;
                    linkBlock(cfg,i,offset);
                    break;

                case PACKED_SWITCH:
                case SPARSE_SWITCH:
                    linkBlock(cfg,i,offset);
                    for (Object k:switch_des){
                        linkBlock(cfg,i,(int)k);
                    }
                    break;

                case PACKED_SWITCH_PAYLOAD:
                case SPARSE_SWITCH_PAYLOAD:
                case THROW:
                case THROW_VERIFICATION_ERROR:
                case RETURN:
                case RETURN_OBJECT:
                case RETURN_VOID:
                case RETURN_VOID_BARRIER:
                case RETURN_VOID_NO_BARRIER:
                case RETURN_WIDE:
                    break;
                default:
                    linkBlock(cfg,i,offset);
            }
        }
        System.out.println(offsets.toString());
        return cfg;
    }

    /**
     * link an edge from BasicBlock (bb) to a BasicBlock started at offset
     * */
    private static void linkBlock(CFG cfg, BasicBlock bb, int offset) {
        for (BasicBlock basicBlock : cfg.blocks) {
            if (basicBlock.getStartAddress() == offset) {
                bb.addSuccessor(basicBlock);
                return;
            }
        }
        // Typically, no exception will be thrown.
        throw new RuntimeException("no basic block found at offset: " + offset);
    }

    public BasicBlock getEntryBB() {return entryBB;}

    public Method getTargetMethod(){
        return this.targetMethod;
    }

    public HashSet<BasicBlock> getBasicBlocks() {
        return blocks;
    }
}
