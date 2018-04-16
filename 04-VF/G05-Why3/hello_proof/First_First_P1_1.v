(* This file is generated by Why3's Coq driver *)
(* Beware! Only edit allowed sections below    *)
Require Import BuiltIn.
Require BuiltIn.

Axiom t : Type.
Parameter t_WhyType : WhyType t.
Existing Instance t_WhyType.

Parameter c: t.

Parameter a: Prop.

Parameter p: t -> Prop.

Parameter q: t -> Prop.

Parameter f: t -> t.

(* Why3 goal *)
Theorem P1 : (forall (x:t), (p x)) -> exists x:t, (p x).
intros.
exists c.
apply H.
Qed.
